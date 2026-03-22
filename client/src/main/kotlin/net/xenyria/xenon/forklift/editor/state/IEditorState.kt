package net.xenyria.xenon.forklift.editor.state

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.message.Message
import org.joml.Vector2d

data class GizmoAxisIntersection(val distance: Double, val axis: Axis)

enum class GizmoInteractionResult {
    NONE,  // No interaction happened.
    START_EDIT,  // The player started editing the object.
    END_EDIT // The player ended editing the object.
}

abstract class IEditorState(val game: IGameClient, val target: IEditorTarget) {

    abstract fun render(renderer: IGameRenderer, selected: Boolean, index: Int)

    abstract fun querySelectedAxis(): GizmoAxisIntersection?

    fun getSelectedAxis(): Axis? {
        return querySelectedAxis()?.axis
    }

    abstract fun onInteract(event: MouseButtonEvent): GizmoInteractionResult

    abstract fun handleMouseMovement(movement: Vector2d)

    abstract val type: EditorMode

    abstract fun getStatus(): Message?
}
