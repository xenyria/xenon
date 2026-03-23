package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.config.XenonConfig
import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.forklift.TransformationMode
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.overlay.EditorOverlayManager
import net.xenyria.xenon.forklift.editor.shape.ShapeManager
import net.xenyria.xenon.forklift.editor.state.GizmoInteractionResult
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.editor.state.impl.RotateState
import net.xenyria.xenon.forklift.editor.state.impl.ScaleState
import net.xenyria.xenon.forklift.editor.state.impl.TranslateState
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.editor.target.TargetManager
import net.xenyria.xenon.forklift.editor.target.TrackedTarget
import net.xenyria.xenon.forklift.gizmo.AXIS_X_COLOR
import net.xenyria.xenon.forklift.gizmo.AXIS_Y_COLOR
import net.xenyria.xenon.forklift.gizmo.AXIS_Z_COLOR
import net.xenyria.xenon.forklift.gizmo.GizmoData
import net.xenyria.xenon.forklift.overlay.TextOverlayData
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import net.xenyria.xenon.message.MessageFormatter
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundRequestModeSwitchPacket
import net.xenyria.xenon.shape.IEditorShape
import org.joml.Vector2d
import org.joml.Vector3dc
import java.awt.Color
import java.util.*

data class RenderableGizmo(val target: TrackedTarget, val selected: Boolean, val index: Int)

enum class EditorMode(val index: Int, val displayName: String, val color: Color) {

    TRANSLATE(1, "T", AXIS_Z_COLOR) {
        override fun createMode(game: IGameClient, target: IEditorTarget): IEditorState {
            return TranslateState(game, target)
        }
    },
    SCALE(2, "S", AXIS_Y_COLOR) {
        override fun createMode(game: IGameClient, target: IEditorTarget): IEditorState {
            return ScaleState(game, target)
        }
    },
    ROTATE(3, "R", AXIS_X_COLOR) {
        override fun createMode(game: IGameClient, target: IEditorTarget): IEditorState {
            return RotateState(game, target)
        }
    };

    abstract fun createMode(game: IGameClient, target: IEditorTarget): IEditorState

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

interface IGameClient {
    fun getCamera(): GameCamera

    fun getMousePosition(): Vector2d

    fun sendPacket(packet: IXenonPacket)

    fun getScreenPosition(worldPosition: Vector3dc): Vector2d

    fun sendMessage(message: Message)

    fun hasShiftDown(): Boolean
    fun hasControlDown(): Boolean
    fun hasAltDown(): Boolean

    /**
     * This updates the last X&Y values Minecraft has received from the mouse. We fake this to prevent the camera from
     * moving too much after the player has moved the gizmo.
     */
    fun updateInternalMousePosition(x: Double, y: Double)

    fun getPlayerId(): UUID?

    fun renderGizmos(renderList: List<RenderableGizmo>)
    fun renderShapes(shapes: List<IEditorShape<*>>)
    fun renderOverlays(overlays: List<TextOverlayData>)

    fun startSession(canUseEditMode: Boolean)

    fun getModVersion(): String

    fun debounceGizmoPacket(packet: ServerboundUpdateGizmoPacket)

    fun isDragging(): Boolean

    fun updateActivity(activityData: ActivityData)
    fun updateActivityAppId(appId: Long)

    val forkliftConfig: ForkliftConfig
    val xenonConfig: XenonConfig

    val editor: Editor
}

class Editor(val client: IGameClient) {

    val dragHandler = EditorDragHandler(client)
    val targetManager = TargetManager(client)
    val shapeManager = ShapeManager(client)
    val overlayManager = EditorOverlayManager(client)
    val timeoutManager = TimeoutManager()
    var isActive: Boolean = true

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
    fun onMouseMove(position: Vector2d) {
        if (!isActive) return
        val gizmo = getActiveGizmo() ?: return
        val delta = dragHandler.getMouseMovementDelta(position.x, position.y)
        gizmo.handleMouseMovement(delta)
        client.updateInternalMousePosition(position.x, position.y)
    }

    @Synchronized
    fun onMouseButton(event: MouseButtonEvent): Boolean {
        if (!isActive) return false
        for (candidate in targetManager.getSortedTargets()) {
            val result = candidate.onInteract(event)
            if (result == GizmoInteractionResult.NONE) continue

            if (result == GizmoInteractionResult.START_EDIT) {
                if (!targetManager.canEditTarget(candidate.target.uuid)) {
                    client.sendMessage(MessageFormatter.formatForkliftMessage("forklift_target_already_being_edited"))
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
        return Message(MessageComponent("Idle", Color(64, 64, 64)))
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

    fun toggleEditMode(): Boolean {
        if (!isActive) {
            enterEditMode()
        }
        isActive = !isActive

        if (isActive) {
            client.sendMessage(MessageFormatter.formatForkliftMessage("forklift_entered_edit_mode"))
        } else {
            client.sendMessage(MessageFormatter.formatForkliftMessage("forklift_exited_edit_mode"))
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
                client.sendMessage(MessageFormatter.formatForkliftMessage("forklift_edit_mode_timeout"))
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

    fun updateOverlays(overlays: List<TextOverlayData>) {
        overlayManager.updateOverlays(overlays)
    }

}