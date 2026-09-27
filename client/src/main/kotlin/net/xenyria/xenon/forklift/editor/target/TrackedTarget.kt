package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.GizmoAxisIntersection
import net.xenyria.xenon.forklift.editor.state.GizmoInteractionResult
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.render.IGameRenderContext
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundClickGizmoPacket
import org.joml.Vector2d

/**
 * Represents a target that is being tracked by the [TargetManager].
 * It manages the state of the target along with interactions and rendering.
 */
class TrackedTarget(
    val game: IEditorGameClient,
    val target: IEditorTarget,
    initialMode: EditorMode
) {

    // Holds the current state of the target (changes whenever a different editor mode gets picked)
    private var state: IEditorState = initialMode.createMode(game, target)

    /**
     * Updates which editor mode should be activated for this target.
     */
    fun setMode(mode: EditorMode) {
        if (mode == state.type) return
        state = mode.createMode(game, target)
    }

    /**
     * Extracts the current state of the gizmo for rendering.
     */
    fun extract(renderer: IGameRenderContext, isSelected: Boolean, isTransparent: Boolean) {
        state.extract(renderer, isSelected, isTransparent)
    }

    /**
     * Event function called when the user interacts with the gizmo using the mouse.
     */
    fun onMouseButtonEvent(mouse: MouseButtonEvent): GizmoInteractionResult {
        if (mouse.isMiddleMouseButton && mouse.isPressed) {
            // Middle-clicking a gizmo sends a packet to the server for editing other properties of the selected entity
            game.sendPacket(ServerboundClickGizmoPacket(target.uuid))
            return GizmoInteractionResult.NONE
        }
        return state.onMouseButtonEvent(mouse)
    }

    /**
     * Event function called when the user moves the mouse.
     */
    fun onMouseMovement(delta: Vector2d) {
        state.onMouseMovement(delta)
    }

    /**
     * Returns a status message for the current state of the target, or null if no message is available.
     */
    fun getStatusMessage(): Message? {
        if (!supportsCurrentMode()) return null
        return state.getStatus()
    }

    /**
     * Queries which axis the user is currently looking at, including the intersection & distance, or null if no axis
     * is selected.
     */
    fun querySelectionState(): GizmoAxisIntersection? {
        return state.querySelectedAxis()
    }

    /**
     * Returns an error message if the current mode is not suitable for this target, or null if no error is present.
     */
    fun getErrorMessage(): String? {
        if (!supportsCurrentMode()) return "forklift_unsupported_mode"
        return null
    }

    /**
     * States whether the current mode is supported by this target.
     */
    fun supportsCurrentMode(): Boolean {
        return target.supportedModes.contains(state.type)
    }

}