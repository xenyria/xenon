package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.TransformationMode
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.overlay.EditorOverlayManager
import net.xenyria.xenon.forklift.editor.shape.ShapeManager
import net.xenyria.xenon.forklift.editor.state.GizmoInteractionResult
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.editor.state.rotate.RotateState
import net.xenyria.xenon.forklift.editor.state.scale.ScaleState
import net.xenyria.xenon.forklift.editor.state.translate.TranslateState
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.editor.target.TargetManager
import net.xenyria.xenon.forklift.editor.target.TrackedTarget
import net.xenyria.xenon.forklift.gizmo.GizmoData
import net.xenyria.xenon.forklift.overlay.TextOverlay
import net.xenyria.xenon.game.IGameClient
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import net.xenyria.xenon.message.MessageFormatter
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundRequestModeSwitchPacket
import net.xenyria.xenon.shape.IEditorShape
import org.joml.Vector2d
import java.awt.Color
import java.util.*

data class RenderableGizmo(
    val target: TrackedTarget,
    val isSelected: Boolean,
    val isTransparent: Boolean,
    val error: String? = null
) {
    val cullingBox: Box
        get() = makeCenteredBox(target.target.position, 3.0, 3.0)
}

/**
 * Enum representing the different editor modes available in Forklift.
 */
enum class EditorMode(val index: Int, val displayName: String, val color: Color) {

    TRANSLATE(1, "T", AXIS_Z_COLOR) {
        override fun createMode(client: IEditorGameClient, target: IEditorTarget): IEditorState {
            return TranslateState(client, target)
        }
    },
    SCALE(2, "S", AXIS_Y_COLOR) {
        override fun createMode(client: IEditorGameClient, target: IEditorTarget): IEditorState {
            return ScaleState(client, target)
        }
    },
    ROTATE(3, "R", AXIS_X_COLOR) {
        override fun createMode(client: IEditorGameClient, target: IEditorTarget): IEditorState {
            return RotateState(client, target)
        }
    };

    abstract fun createMode(client: IEditorGameClient, target: IEditorTarget): IEditorState

    companion object {
        fun byId(id: Int): EditorMode? {
            return entries.find { it.index == id }
        }

        fun from(mode: TransformationMode): EditorMode {
            return when (mode) {
                TransformationMode.TRANSLATE -> TRANSLATE
                TransformationMode.ROTATE -> ROTATE
                TransformationMode.SCALE -> SCALE
            }
        }
    }
}

/**
 * Abstract interface for editor clients. (Game clients with support for Xenon's Forklift/Editor feature)
 */
interface IEditorGameClient : IGameClient {

    /**
     * Instructs the client to render the given gizmos for the next frame.
     * The supplied state is kept until the next call to this method happens.
     */
    fun renderGizmos(renderList: List<RenderableGizmo>)

    /**
     * Instructs the client to render the given shapes on the next frame.
     * The supplied state is kept until the next call to this method happens.
     */
    fun renderShapes(shapes: List<IEditorShape<*>>)

    /**
     * Instructs the client to render the given text overlays on the next frame.
     * The supplied state is kept until the next call to this method happens.
     */
    fun renderOverlays(overlays: List<TextOverlay>)

    /**
     * States whether the user is currently performing a drag operation.
     */
    fun isDragging(): Boolean

    /**
     * Debounces a gizmo packet, basically queueing the packet to be sent on the next tick.
     * If another call to this function is made before the next tick, the previous packet is discarded and replaced with
     * the new one.
     */
    fun debounceGizmoPacket(packet: ServerboundUpdateGizmoPacket)

    /**
     * Configuration for Forklift.
     */
    val forkliftConfig: ForkliftConfig

    /**
     * The currently active editor client state.
     */
    val editorState: EditorClientState
}

/**
 * Main class for an editor client.
 */
class EditorClientState(val client: IEditorGameClient) {

    val dragHandler = EditorDragHandler(client)
    val targetManager = TargetManager(client)
    val shapeManager = ShapeManager(client)
    val overlayManager = EditorOverlayManager(client)
    val timeoutManager = TimeoutManager()
    var isActive: Boolean = false

    @Synchronized
    fun updateSelectedGizmo(gizmoId: UUID?) {
        targetManager.updateSelectedGizmo(gizmoId)
    }

    @Synchronized
    fun onTick() {
        timeoutManager.timeoutExpiredRequests()
        targetManager.onTick()
    }

    fun reset() {
        dragHandler.reset()
        targetManager.reset()
        timeoutManager.timeoutExpiredRequests()
        shapeManager.reset()
        overlayManager.reset()
    }

    fun selectMode(numberKey: Int) {
        val mode = EditorMode.byId(numberKey) ?: return
        targetManager.selectMode(mode)
    }

    fun getActiveGizmo(): TrackedTarget? {
        return targetManager.getActiveTarget()
    }

    fun isMouseLocked(): Boolean {
        return dragHandler.isActive()
    }

    @Synchronized
    fun onMouseMove(delta: Vector2d) {
        if (!isActive) return
        val gizmo = getActiveGizmo() ?: return

        val gizmoDelta = dragHandler.getDragMouseDelta(delta.x, delta.y)
        gizmo.onMouseMovement(gizmoDelta)
        client.updateInternalMousePosition(delta.x, delta.y)
    }

    @Synchronized
    fun onMouseButton(event: MouseButtonEvent): Boolean {
        if (!isActive) return false
        for (candidate in targetManager.getSortedTargets()) {
            if (!candidate.supportsCurrentMode()) continue
            val result = candidate.onMouseButtonEvent(event)
            if (result == GizmoInteractionResult.NONE) continue

            if (result == GizmoInteractionResult.START_EDIT) {
                if (!targetManager.canEditTarget(candidate.target.uuid)) {
                    client.displayChatMessage(MessageFormatter.formatForkliftMessage("forklift_target_already_being_edited"))
                    return false
                }

                // We should be able to edit this object now.
                val packet = ServerboundRequestGizmoPacket(candidate.target.uuid)
                client.sendPacket(packet)
                targetManager.setActiveTarget(candidate)
            } else if (result == GizmoInteractionResult.END_EDIT) {
                targetManager.releaseTarget()
            }
            return true
        }
        return false
    }

    @Synchronized
    fun enableDragMode(uuid: UUID) {
        dragHandler.enableDragMode(uuid)
    }

    @Synchronized
    fun isSelected(targetId: UUID): Boolean {
        return targetManager.getActiveTarget()?.target?.uuid == targetId
    }

    fun leaveDragMode() {
        dragHandler.exitDragMode()
    }

    /**
     * Returns the current status message for the editor.
     */
    fun getStatusMessage(): Message {
        val gizmo = getActiveGizmo()
        if (gizmo != null) {
            val msg = gizmo.getStatusMessage()
            if (msg != null) return msg
        }
        for (target in targetManager.getAvailableTargets()) {
            val msg = target.getStatusMessage() ?: continue
            return msg
        }
        return Message(MessageComponent("Idle", Color(128, 128, 128)))
    }

    fun getModeMessage(): Message {
        val components = ArrayList<MessageComponent>()
        val activeMode = targetManager.getActiveMode()
        for ((index, mode) in EditorMode.entries.withIndex()) {
            var text = mode.displayName
            if (index != EditorMode.entries.size - 1) text += " "
            if (mode == activeMode) {
                components.add(MessageComponent(text, mode.color))
            } else {
                components.add(MessageComponent(text, Color.GRAY))
            }
        }
        return Message(components)
    }

    fun leaveEditMode() {
        // Exit early if we're already trying to leave edit mode.
        if (timeoutManager.addTimeout(
                "edit-mode",
                onTimeout = {
                    isActive = true
                    client.displayChatMessage(MessageFormatter.formatForkliftMessage("forklift_edit_mode_timeout"))
                })
        ) {
            client.sendPacket(ServerboundRequestModeSwitchPacket(false))
        }
    }

    fun toggleEditMode(): Boolean {
        if (!isActive) {
            enterEditMode()
        } else {
            leaveEditMode()
        }
        return true
    }

    fun acknowledgeEditMode(newState: Boolean) {
        timeoutManager.removeTimeout("edit-mode")
        isActive = newState
    }

    private fun enterEditMode() {
        if (!timeoutManager.addTimeout("edit-mode") {
                isActive = false
                client.displayChatMessage(MessageFormatter.formatForkliftMessage("forklift_edit_mode_timeout"))
            }) {
            return
        }
        client.sendPacket(ServerboundRequestModeSwitchPacket(true))
    }

    fun exitDragMode() {
        dragHandler.exitDragMode()
    }

    fun updateShapes(shapes: List<IEditorShape<*>>) {
        shapeManager.updateShapes(shapes)
    }

    fun resetShapes() {
        shapeManager.reset()
    }

    fun removeShapes(shapeIds: List<String>) {
        shapeManager.removeShapes(shapeIds.toSet())
    }

    fun removeOverlays(overlays: List<String>) {
        overlayManager.removeOverlays(overlays.toSet())
    }

    fun resetOverlays() {
        overlayManager.reset()
    }

    fun updateGizmos(added: List<GizmoData>, removed: List<UUID>, updated: List<GizmoData>) {
        targetManager.updateGizmos(added, removed, updated)
    }

    fun updateOverlays(overlays: List<TextOverlay>) {
        overlayManager.updateOverlays(overlays)
    }
}