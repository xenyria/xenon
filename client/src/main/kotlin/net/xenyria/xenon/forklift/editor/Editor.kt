package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.GizmoInteractionResult
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.editor.state.impl.RotateState
import net.xenyria.xenon.forklift.editor.state.impl.ScaleState
import net.xenyria.xenon.forklift.editor.state.impl.TranslateState
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.editor.target.TargetManager
import net.xenyria.xenon.forklift.editor.target.TrackedTarget
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.serverbound.gizmo.ServerboundRequestGizmoPacket
import org.joml.Vector2d
import org.joml.Vector3dc
import java.util.*

data class RenderableGizmo(val target: TrackedTarget, val selected: Boolean, val index: Int)

enum class EditorMode(val index: Int, val displayName: String) {
    TRANSLATE(1, "T") {
        override fun createMode(game: IGameClient, target: IEditorTarget): IEditorState {
            return TranslateState(game, target)
        }
    },
    SCALE(2, "S") {
        override fun createMode(game: IGameClient, target: IEditorTarget): IEditorState {
            return ScaleState(game, target)
        }
    },
    ROTATE(3, "R") {
        override fun createMode(game: IGameClient, target: IEditorTarget): IEditorState {
            return RotateState(game, target)
        }
    };

    abstract fun createMode(game: IGameClient, target: IEditorTarget): IEditorState

    companion object {
        fun byId(id: Int): EditorMode? {
            return entries.find { it.index == id }
        }
    }
}

interface IGameClient {
    fun getCamera(): GameCamera

    fun getMousePosition(): Vector2d

    fun sendPacket(packet: IXenonPacket)

    fun getScreenPosition(worldPosition: Vector3dc): Vector2d

    fun sendMessage(text: String)

    fun hasShiftDown(): Boolean
    fun hasControlDown(): Boolean
    fun hasAltDown(): Boolean

    /**
     * This updates the last X&Y values Minecraft has received from the mouse. We fake this to prevent the camera from
     * moving too much after the player has moved the gizmo.
     */
    fun updateInternalMousePosition(x: Double, y: Double)

    fun getPlayerId(): UUID?

    fun renderGizmos(renderList: List<RenderableGizmo>) {}

    val config: ForkliftConfig

    val editor: Editor
}

class Editor(val client: IGameClient) {

    val dragHandler = EditorDragHandler(client)
    val targetManager = TargetManager(client)
    var isActive: Boolean = true

    @Synchronized
    fun updateSelectedGizmo(gizmoId: UUID?) {
        targetManager.updateSelectedGizmo(gizmoId)
    }

    @Synchronized
    fun onTick() {
        targetManager.onTick()
    }

    fun reset() {
        dragHandler.reset()
        targetManager.reset()
    }

    fun getCamera(): GameCamera {
        return client.getCamera()
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
        for (candidate in targetManager.getAvailableTargets()) {
            val result = candidate.onInteract(event)
            if (result == GizmoInteractionResult.NONE) continue

            if (result == GizmoInteractionResult.START_EDIT) {
                if (!targetManager.canEditTarget(candidate.target.uuid)) {
                    client.sendMessage("This object is currently being edited by another player.")
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

}