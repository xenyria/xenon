package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.GizmoAxisIntersection
import net.xenyria.xenon.forklift.editor.state.GizmoInteractionResult
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundClickGizmoPacket
import org.joml.Vector2d

class TrackedTarget(val game: IGameClient, val target: IEditorTarget, initialMode: EditorMode) {

    private var _state: IEditorState = initialMode.createMode(game, target)

    fun setMode(mode: EditorMode) {
        if (mode == _state.type) return
        _state = mode.createMode(game, target)
    }

    fun render(renderer: IGameRenderer, isSelected: Boolean, isTransparent: Boolean) {
        _state.render(renderer, isSelected, isTransparent)
    }

    fun onInteract(mouse: MouseButtonEvent): GizmoInteractionResult {
        if (mouse.isMiddleMouseButton && mouse.isPressed) {
            // Middle-clicking a gizmo sends a packet to the server for editing other properties of the selected entity
            game.sendPacket(ServerboundClickGizmoPacket(target.uuid))
            return GizmoInteractionResult.NONE
        }
        return _state.onInteract(mouse)
    }

    fun handleMouseMovement(delta: Vector2d) {
        _state.handleMouseMovement(delta)
    }

    fun getStatusMessage(): Message? {
        if (!supportsCurrentMode()) return null
        return _state.getStatus()
    }

    fun querySelectionState(): GizmoAxisIntersection? {
        return _state.querySelectedAxis()
    }

    fun getErrorMessage(): String? {
        if (!supportsCurrentMode()) return "forklift_unsupported_mode"
        return null
    }

    fun supportsCurrentMode(): Boolean {
        return target.supportedModes.contains(_state.type)
    }

}