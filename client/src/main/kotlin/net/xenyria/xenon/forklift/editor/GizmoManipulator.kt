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

    fun getMovementDelta(
        game: IGameClient,
        objectPosition: Vector3dc,
        axisDirection: Vector3dc,
        cursorPos: Vector2d
    ): Double {
        // The idea is to calculate two points on the screen to form a line.
        // Based on this line, we'll calculate how far the cursor has traveled parallel to the line.
        val pointA = Vector3d(objectPosition).add(Vector3d(axisDirection).mul(-128.0))
        val pointB = Vector3d(objectPosition).add(Vector3d(axisDirection).mul(128.0))

        // Map 3D coordinates to 2D coordinates
        val from = game.getScreenPosition(pointA)
        val to = game.getScreenPosition(pointB)

        val objectPosOnScreen = game.getScreenPosition(objectPosition)
        if ((from.x() == 0.0 && from.y() == 0.0) || (to.x() == 0.0 && to.y() == 0.0)) return 0.0

        //val isOnPositiveSide = false
        val direction = Vector2d(to.x(), to.y()).sub(Vector2d(from.x(), from.y())).normalize()

        //val testPosA = Vector2d(objectPosOnScreen).add(Vector2d(direction).mul(8.0))
        //val testPosB = Vector2d(objectPosOnScreen).add(Vector2d(direction).mul(-8.0))

        val deltaToCenter = Vector2d(objectPosOnScreen).sub(cursorPos)
        val dot = deltaToCenter.dot(direction)

        if (dot.isInfinite() || dot.isNaN()) return 0.0
        return dot * 1
    }

    fun calculateGizmoDelta(
        game: IGameClient,
        axis: Axis,
        direction: Vector3dc,
        target: IEditorTarget
    ): GizmoOffset {
        val origin = game.editor.dragHandler.getMousePositionBeforeDrag()
        val currentMousePos = game.editor.dragHandler.getTotalDragDelta()

        // Determine how far the cursor has moved from the origin
        val originalDelta = getMovementDelta(
            game, target.position, Vector3d(direction),
            Vector2d(origin.x, origin.y)
        )
        val newDelta = getMovementDelta(
            game, target.position, Vector3d(direction),
            currentMousePos
        )
        game.editor.dragHandler.resetDragCenter()

        val displacementDelta = newDelta - originalDelta
        return GizmoOffset(axis, displacementDelta)
    }


}