package net.xenyria.xenon.forklift.editor.state

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.IGameRenderContext
import net.xenyria.xenon.message.Message
import org.joml.Vector2d

data class GizmoAxisIntersection(val distance: Double, val axis: Axis)

enum class GizmoInteractionResult {
    // No interaction happened.
    NONE,

    // The player started editing the object.
    START_EDIT,

    // The player ended editing the object.
    END_EDIT
}

/**
 * Represents the state of the editor for a specific target.
 * Implementations of this class are expected to handle rendering (extraction) and user interactions for the target.
 */
abstract class IEditorState(val client: IEditorGameClient, val target: IEditorTarget) {

    /**
     * Extracts the current state of the gizmo for rendering.
     */
    abstract fun extract(renderer: IGameRenderContext, isSelected: Boolean, isTransparent: Boolean)

    /**
     * Queries which axis the user is currently looking at, including the intersection & distance, or null if no axis
     * is selected.
     */
    abstract fun querySelectedAxis(): GizmoAxisIntersection?

    /**
     * Returns which axis the user is currently looking at, or null if no axis is selected.
     */
    fun getSelectedAxis(): Axis? {
        return querySelectedAxis()?.axis
    }

    /**
     * Event function called when the user interacts with the gizmo using the mouse.
     * The returned [GizmoInteractionResult] controls the new state of the editor.
     */
    abstract fun onMouseButtonEvent(event: MouseButtonEvent): GizmoInteractionResult

    /**
     * Event function called when the user moves the mouse.
     */
    abstract fun onMouseMovement(movement: Vector2d)

    abstract val type: EditorMode

    abstract fun getStatus(): Message?
}
