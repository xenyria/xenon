package net.xenyria.xenon.forklift.editor.state

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.forklift.editor.GizmoManipulator
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.GizmoRotationHelper.getVectorsForAxis
import net.xenyria.xenon.forklift.editor.state.GizmoRotationHelper.translateAxis
import net.xenyria.xenon.forklift.editor.state.GizmoRotationHelper.translateAxisForControls
import net.xenyria.xenon.forklift.editor.state.impl.ROTATION_GIZMO_RADIUS
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.roundToNearestMultiple
import org.joml.Quaternionf
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import java.lang.Math

const val ROTATION_SENSITIVITY = 0.7
const val ROTATION_FINE_SENSITIVITY = 0.12

const val EDIT_PLANE_SIZE = 32
const val EDIT_AXIS_SIZE = 0.0001

data class CirclePoint(val vector: Vector3dc, val angle: Double)
data class AxisPoint(val axis: Axis, val point: CirclePoint) : IIntersectable {
    override val boundingBoxes: List<Box> get() = listOf(makeCenteredBox(point.vector, 0.1, 0.1))
}

object GizmoRotationHelper {

    fun translateAxis(axis: Axis): Axis {
        if (axis === Axis.X) return Axis.Z
        if (axis === Axis.Z) return Axis.X
        return axis
    }

    fun translateAxisForControls(axis: Axis): Axis {
        if (axis === Axis.X) return Axis.Y
        if (axis === Axis.Z) return Axis.Y
        if (axis === Axis.Y) return Axis.Z
        return axis
    }

    fun getVectorsForAxis(origin: Vector3dc, axis: Axis): List<CirclePoint> {
        val resolution = 2.5
        return when {
            axis === Axis.X -> getCircleVectors(origin, ROTATION_GIZMO_RADIUS, 0f, true, resolution)
            axis === Axis.Y -> getCircleVectors(origin, ROTATION_GIZMO_RADIUS, 0f, false, resolution)
            axis === Axis.Z -> getCircleVectors(origin, ROTATION_GIZMO_RADIUS, 90f, true, resolution)
            else -> emptyList()
        }
    }

    fun getCircleVectors(center: Vector3dc, radius: Double, yaw: Float, horizontal: Boolean, resolution: Double): List<CirclePoint> {
        val vectors = ArrayList<CirclePoint>()

        var rotation = 0.0
        while (rotation < 360.0) {
            val centerVector = Vector3d(center)
            if (!horizontal) {
                centerVector.add(calculateDirection(yaw + rotation, 0).mul(radius))
            } else {
                centerVector.add(calculateDirection(yaw, rotation).mul(radius))
            }
            vectors.add(CirclePoint(centerVector, rotation))
            rotation += resolution
        }
        return vectors
    }


    fun getSphereEditingBox(origin: Vector3dc, axis: Axis): OBB {
        return when (axis) {
            Axis.X -> {
                // Create a OBB along the X axis
                OBB(
                    origin,
                    EDIT_PLANE_SIZE.toDouble(),
                    EDIT_PLANE_SIZE.toDouble(),
                    EDIT_AXIS_SIZE
                )
            }

            Axis.Y -> {
                OBB(
                    origin,
                    EDIT_PLANE_SIZE.toDouble(),
                    EDIT_AXIS_SIZE,
                    EDIT_PLANE_SIZE.toDouble(),
                )
            }

            Axis.Z -> {
                // Create a OBB along the Z axis
                OBB(
                    origin,
                    EDIT_AXIS_SIZE,
                    EDIT_PLANE_SIZE.toDouble(),
                    EDIT_PLANE_SIZE.toDouble()
                )
            }
        }
    }

}


class GizmoRotator(val game: IGameClient, val target: IEditorTarget) {

    private var _quaternion: Quaternionf? = null
    private var _previousCameraRotation = Vector3d()
    private var _previousObjectRotation = Vector3d()

    fun resetSelectedAxis() {
        editingAxis = null
        _quaternion = null
    }

    fun getSnapValue(game: IGameClient): Double {
        return game.config.rotationGridSnap
    }

    fun onMouseMove(game: IGameClient, movement: Vector2d) {
        val editingAxis = this.editingAxis
        if (editingAxis == null || !target.supportedRotationAxes.contains(editingAxis)) return
        val mouseX: Double = game.getMousePosition().x()
        val mouseY: Double = game.getMousePosition().y()
        val sensitivity = if (game.hasShiftDown()) ROTATION_FINE_SENSITIVITY else ROTATION_SENSITIVITY

        // Store previous mouse coordinates
        if (_lastMouseX == Double.MIN_VALUE || _lastMouseY == Double.MIN_VALUE) {
            _lastMouseX = mouseX
            _lastMouseY = mouseY
        }
        val delta = GizmoManipulator.calculateMovementDelta(
            game,
            translateAxisForControls(editingAxis),
            translateAxisForControls(editingAxis).positive, target
        )
        val displacement = delta.displacement * sensitivity

        val quaternion = _quaternion
        if (quaternion != null) {
            when (editingAxis) {
                Axis.X -> quaternion.rotateLocalX(Math.toRadians(-displacement).toFloat())
                Axis.Y -> quaternion.rotateLocalY(Math.toRadians(displacement).toFloat())
                Axis.Z -> quaternion.rotateLocalZ(Math.toRadians(displacement).toFloat())
            }

            val buffer = Vector3f()
            quaternion.getEulerAnglesYXZ(buffer)

            var degrees = Vector3d(
                Math.toDegrees(buffer.x.toDouble()),
                Math.toDegrees(buffer.y.toDouble()),
                Math.toDegrees(buffer.z.toDouble())
            )
            if (game.hasControlDown())
                degrees = Vector3d(roundToNearestMultiple(degrees, getSnapValue(game)))
            target.rotation = degrees
        }

        _lastMouseX = mouseX
        _lastMouseY = mouseY
    }

    fun getEffectiveRotation(): Vector3d {
        return target.rotation.sub(_previousObjectRotation)
    }

    fun onInteract(event: MouseButtonEvent): GizmoInteractionResult {
        if (!event.isRightMouseButton) return GizmoInteractionResult.NONE
        if (event.isReleased) {
            editingAxis = null
            return GizmoInteractionResult.END_EDIT
        } else if (event.isPressed) {
            val editingAxis = getSelectedAxis()
            this.editingAxis = editingAxis
            if (editingAxis == null) return GizmoInteractionResult.NONE

            _previousObjectRotation = Vector3d(target.rotation)
            _previousCameraRotation = Vector3d(game.getCamera().direction)

            val obb = GizmoRotationHelper.getSphereEditingBox(
                target.position,
                translateAxis(editingAxis)
            )
            val camera = game.getCamera()
            val intersection = obb.intersection(camera.position, camera.direction)
                ?: return GizmoInteractionResult.NONE

            if (_quaternion == null) {
                val quaternion = Quaternionf()
                quaternion.rotateY(Math.toRadians(_previousObjectRotation.y).toFloat())
                quaternion.rotateX(Math.toRadians(_previousObjectRotation.x).toFloat())
                quaternion.rotateZ(Math.toRadians(_previousObjectRotation.z).toFloat())
                this._quaternion = quaternion
            }

            game.editor.enableDragMode(target.uuid)
            return GizmoInteractionResult.START_EDIT
        }
        return GizmoInteractionResult.NONE
    }


    private fun getSelectedAxis(): Axis? {
        return querySelectedAxis()?.axis
    }

    private fun querySelectedAxisPoint(camera: GameCamera): Pair<AxisPoint, Double>? {
        val cameraPosition = camera.position
        if (cameraPosition.distance(target.position) > 7) return null

        val cameraDirection = camera.direction
        val axisPoints = ArrayList<AxisPoint>()
        for (axis in target.supportedRotationAxes) {
            for (point in getVectorsForAxis(target.position, axis)) axisPoints.add(AxisPoint(axis, point))
        }
        val closestIntersection = RayCast.findNearestIntersection(axisPoints, cameraPosition, cameraDirection, 10.0) ?: return null
        return closestIntersection.data to closestIntersection.intersection.distance
    }

    fun querySelectedAxis(): GizmoAxisIntersection? {
        val (point, distance) = querySelectedAxisPoint(game.getCamera()) ?: return null
        return GizmoAxisIntersection(distance, point.axis)
    }

    var editingAxis: Axis? = null
        private set
    private var _lastMouseX: Double = Double.MIN_VALUE
    private var _lastMouseY: Double = Double.MIN_VALUE

}