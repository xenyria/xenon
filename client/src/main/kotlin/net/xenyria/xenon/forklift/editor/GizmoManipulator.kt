package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3dc

data class MovementDelta(val axis: Axis, val displacement: Double)

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
        val pointA = Vector3d(objectPosition).add(Vector3d(axisDirection))
        val pointB = Vector3d(objectPosition).add(Vector3d(axisDirection).mul(-1.0))

        // Map 3D coordinates to 2D coordinates
        val from = game.getScreenPosition(pointA)
        val to = game.getScreenPosition(pointB)

        val objectPosOnScreen = game.getScreenPosition(objectPosition)

        // Find the closest intersection point
        //val closestPoint = findClosestPointOnLine(from, to, cursorPos)
        //val delta = closestPoint.distance(objectPosOnScreen)

        if ((from.x() == 0.0 && from.y() == 0.0) || (to.x() == 0.0 && to.y() == 0.0)) return 0.0

        //val isOnPositiveSide = false
        val direction = Vector2d(to.x(), to.y()).sub(Vector2d(from.x(), from.y())).normalize()

        //val testPosA = Vector2d(objectPosOnScreen).add(Vector2d(direction).mul(8.0))
        //val testPosB = Vector2d(objectPosOnScreen).add(Vector2d(direction).mul(-8.0))

        val deltaToCenter = Vector2d(objectPosOnScreen).sub(cursorPos)
        val dot = deltaToCenter.dot(direction)

        if (dot.isInfinite() || dot.isNaN()) {
            return 0.0
        }

        return dot * -1
    }

    fun calculateMovementDelta(
        game: IGameClient,
        axis: Axis,
        direction: Vector3dc,
        target: IEditorTarget
    ): MovementDelta {
        val mousePos = game.getMousePosition()
        if (lastMouseX == Double.MIN_VALUE) {
            val origin = game.editor.dragHandler.getMousePositionBeforeDrag()
            lastMouseX = origin.x()
            lastMouseY = origin.y()
        }

        val originalDelta = getMovementDelta(
            game, target.position, Vector3d(direction),
            Vector2d(lastMouseX.toInt().toDouble(), lastMouseY.toInt().toDouble())
        )
        val newDelta = getMovementDelta(
            game, target.position, Vector3d(direction),
            game.getMousePosition()
        )
        lastMouseX = mousePos.x()
        lastMouseY = mousePos.y()

        val displacementDelta = newDelta - originalDelta
        return MovementDelta(axis, displacementDelta)
    }


}