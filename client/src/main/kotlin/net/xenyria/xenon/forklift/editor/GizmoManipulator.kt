package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3dc

data class GizmoOffset(val axis: Axis, val displacement: Double)

object GizmoManipulator {

    private var lastMouseX: Double = Double.MIN_VALUE
    private var lastMouseY: Double = Double.MIN_VALUE

    fun reset() {
        lastMouseX = Double.MIN_VALUE
        lastMouseY = Double.MIN_VALUE
    }

    /**
     * Calculates how far the cursor has moved along the given axis direction based on the current mouse
     * position.
     * Axis direction & object position are both world space 3D coordinates, while cursor position is the X & Y position
     * of the mouse on the screen.
     */
    fun getMovementDelta(
        game: IEditorGameClient,
        objectPosition: Vector3dc,
        axisDirection: Vector3dc,
        cursorPos: Vector2d
    ): Double {
        // The idea is to calculate two points on the screen to form a line.
        // Based on this line, we'll calculate how far the cursor has traveled parallel to the line.

        // We take the camera position and add the current direction vector to it to get a point a few blocks in front
        // of the camera. This ensures that both points will end up being on the screen.
        val cameraPos = Vector3d(game.getCamera().position).add(Vector3d(game.getCamera().direction).mul(4.0))

        // Based on this point, we calculate two points to form the axis line in world space.
        val pointA = Vector3d(cameraPos).add(Vector3d(axisDirection).mul(-1.0))
        val pointB = Vector3d(cameraPos).add(Vector3d(axisDirection).mul(1.0))

        // Then we calculate where these points are on the screen
        val from = game.getScreenPosition(pointA)
        val to = game.getScreenPosition(pointB)
        if ((from.x() == 0.0 && from.y() == 0.0) || (to.x() == 0.0 && to.y() == 0.0)) return 0.0

        val screenDirection = Vector2d(to).sub(Vector2d(from))
        // In some cases, the line ends up being so short.
        // (e.g. when the axis direction perfectly matches the camera direction)
        // To prevent moving the object thousands of blocks into some direction, we just bail out early.
        if (screenDirection.length() < 0.001) return 0.0
        val direction = screenDirection.normalize()
        if (!direction.isFinite) return 0.0

        // Lastly we calculate where the object is on the screen, that way we can calculate where the mouse cursor is
        // relative to the object.
        val objectPosOnScreen = game.getScreenPosition(objectPosition)
        val deltaToCenter = Vector2d(objectPosOnScreen).sub(cursorPos)
        val dot = deltaToCenter.dot(direction)

        if (dot.isInfinite() || dot.isNaN()) return 0.0
        return dot
    }

    /**
     * Calculates how far the gizmo has moved based on the mouse movement and the axis direction.
     */
    fun calculateGizmoMovementDelta(
        game: IEditorGameClient,
        axis: Axis,
        direction: Vector3dc,
        target: IEditorTarget
    ): GizmoOffset {
        val origin = game.editorState.dragHandler.getMousePositionBeforeDrag()
        val currentMousePos = game.editorState.dragHandler.getTotalDragDelta()

        // Determine how far the cursor has moved from the origin
        val originalDelta = getMovementDelta(
            game, target.position, Vector3d(direction),
            Vector2d(origin.x, origin.y)
        )
        val newDelta = getMovementDelta(
            game, target.position, Vector3d(direction),
            currentMousePos
        )
        game.editorState.dragHandler.resetDragCenter()

        val displacementDelta = newDelta - originalDelta
        return GizmoOffset(axis, displacementDelta)
    }


}