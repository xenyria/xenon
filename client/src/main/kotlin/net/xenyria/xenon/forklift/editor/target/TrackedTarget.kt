package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.GizmoInteractionResult
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.message.Message
import org.joml.Vector2d

class TrackedTarget(val game: IGameClient, val target: IEditorTarget, initialMode: EditorMode) {

    private var _state: IEditorState = initialMode.createMode(game, target)

    fun setMode(mode: EditorMode) {
        if (mode == _state.type) return
        _state = mode.createMode(game, target)
    }

    fun render(renderer: IGameRenderer, selected: Boolean, index: Int) {
        _state.render(renderer, selected, index)
    }

    fun onInteract(mouse: MouseButtonEvent): GizmoInteractionResult {
        return _state.onInteract(mouse)
    }

    fun handleMouseMovement(delta: Vector2d) {
        _state.handleMouseMovement(delta)
    }

    fun getStatusMessage(): Message? {
        return _state.getStatus()
    }

}