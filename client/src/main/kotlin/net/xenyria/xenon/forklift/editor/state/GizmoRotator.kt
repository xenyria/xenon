package net.xenyria.xenon.forklift.editor.state

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.forklift.editor.GizmoManipulator
import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.GizmoRotationHelper.translateAxis
import net.xenyria.xenon.forklift.editor.state.GizmoRotationHelper.translateAxisForControls
import net.xenyria.xenon.forklift.editor.state.rotate.IRotationMode
import net.xenyria.xenon.forklift.editor.state.rotate.ROTATION_GIZMO_RADIUS
import net.xenyria.xenon.forklift.editor.state.rotate.RotationModeParams
import net.xenyria.xenon.forklift.editor.state.rotate.create
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f

const val ROTATION_SENSITIVITY = 0.7
const val ROTATION_FINE_SENSITIVITY = 0.12

const val EDIT_PLANE_SIZE = 32
const val EDIT_AXIS_SIZE = 0.0001

data class CirclePoint(val vector: Vector3dc, val angle: Double)
data class AxisBox(val axis: Axis, val box: Box) : IIntersectable {
    override val boundingBoxes: List<Box> get() = listOf(box)
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

    fun getCircleVectors(
        center: Vector3dc,
        radius: Double,
        yaw: Float,
        horizontal: Boolean,
        resolution: Double
    ): List<CirclePoint> {
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

/**
 * Helper class for rotating objects in the editor.
 */
class GizmoRotator(
    val editor: IEditorGameClient,
    val target: IEditorTarget
) {

    private var rotationMode: IRotationMode? = null

    // The previous & new rotation values. Used for calculating how much the object has rotated since the user
    // started editing it.
    private var previousObjectRotation = Vector3d()
    private var previousLocalRotation = Vector3f()
    private var newLocalRotation = Vector3f()

    fun resetSelectedAxis() {
        editingAxis = null
        rotationMode = null
    }

    fun onMouseMovement(game: IEditorGameClient) {
        val editingAxis = this.editingAxis
        if (editingAxis == null || !target.supportedRotationAxes.contains(editingAxis)) return
        val sensitivity = if (game.hasShiftDown()) ROTATION_FINE_SENSITIVITY else ROTATION_SENSITIVITY

        // Store previous mouse coordinates
        val delta = GizmoManipulator.calculateGizmoMovementDelta(
            game,
            translateAxisForControls(editingAxis),
            translateAxisForControls(editingAxis).positive, target
        )

        val displacement = delta.displacement * sensitivity
        rotationMode?.rotate(displacement, game.hasControlDown())
    }

    fun shouldSnapRotation(): Boolean {
        return editor.hasControlDown()
    }

    fun getEffectiveRotation(): Vector3d {
        return rotationMode?.getEffectiveRotation(shouldSnapRotation()) ?: Vector3d()
    }

    fun onMouseButtonEvent(event: MouseButtonEvent): GizmoInteractionResult {
        if (!event.isRightMouseButton) return GizmoInteractionResult.NONE
        if (event.isReleased) {
            reset()
            return GizmoInteractionResult.END_EDIT
        } else if (event.isPressed) {
            val editingAxis = getSelectedAxis()
            this.editingAxis = editingAxis
            if (editingAxis == null) return GizmoInteractionResult.NONE

            previousObjectRotation = Vector3d(target.rotation)

            val obb = GizmoRotationHelper.getSphereEditingBox(
                target.position,
                translateAxis(editingAxis)
            )
            val camera = editor.getCamera()
            obb.intersection(camera.position, camera.direction) ?: return GizmoInteractionResult.NONE

            rotationMode = target.rotationMode.create(
                RotationModeParams(
                    target.rotation,
                    editor.forkliftConfig,
                    editingAxis
                ) { target.rotation = it }
            )
            editor.editorState.enableDragMode(target.uuid)
            return GizmoInteractionResult.START_EDIT
        }
        return GizmoInteractionResult.NONE
    }


    private fun getSelectedAxis(): Axis? {
        return querySelectedAxis()?.axis
    }

    private fun querySelectedAxisPoint(camera: GameCamera): Pair<Axis, Double>? {
        val cameraPosition = camera.position
        if (cameraPosition.distance(target.position) > 7) return null

        val cameraDirection = camera.direction
        val position = target.position

        val thickness = 0.01
        val xAxisBox = Box(
            position.x - ROTATION_GIZMO_RADIUS,
            position.y - ROTATION_GIZMO_RADIUS,
            position.z - thickness,
            position.x + ROTATION_GIZMO_RADIUS,
            position.y + ROTATION_GIZMO_RADIUS,
            position.z + thickness,
        )
        val zAxisBox = Box(
            position.x - thickness,
            position.y - ROTATION_GIZMO_RADIUS,
            position.z - ROTATION_GIZMO_RADIUS,
            position.x + thickness,
            position.y + ROTATION_GIZMO_RADIUS,
            position.z + ROTATION_GIZMO_RADIUS,
        )

        val axes = mapOf(
            Axis.X to AxisBox(Axis.X, zAxisBox),
            Axis.Y to AxisBox(Axis.Y, makeCenteredBox(position, ROTATION_GIZMO_RADIUS * 2.0, thickness)),
            Axis.Z to AxisBox(Axis.Z, xAxisBox),
        )

        val maxRadius = ROTATION_GIZMO_RADIUS

        val results = ArrayList<Pair<Axis, Double>>()
        for (axis in target.supportedRotationAxes) {
            if (!target.rotationMode.supportedAxes.contains(axis)) continue

            val box = requireNotNull(axes[axis])
            val intersection = RayCast.intersection(box.box, cameraPosition, cameraDirection, 10.0)
            if (intersection != null) {
                val distance = intersection.hitPosition.distance(position)
                val maxDeviance = 0.075
                var isWithinRange = distance >= maxRadius - maxDeviance && distance <= maxRadius + maxDeviance
                if (axis == Axis.Y && (intersection.face != CubeFace.UP && intersection.face != CubeFace.DOWN))
                    isWithinRange = true
                if (isWithinRange)
                    results.add(axis to intersection.distance)
            }
        }
        if (results.isEmpty()) return null
        val result = results.minBy { it.second }
        return result.first to result.second
    }

    fun querySelectedAxis(): GizmoAxisIntersection? {
        val (axis, distance) = querySelectedAxisPoint(editor.getCamera()) ?: return null
        return GizmoAxisIntersection(distance, axis)
    }

    var editingAxis: Axis? = null
        private set

    private fun reset() {
        GizmoManipulator.reset()
        editingAxis = null
        previousObjectRotation.set(0.0)
        previousLocalRotation.set(0.0)
        newLocalRotation.set(0.0)
    }

    fun getPreviousRotation(editingAxis: Axis): Double {
        return rotationMode?.getPreviousRotation(editingAxis) ?: 0.0
    }

    fun getNewRotation(editingAxis: Axis): Double {
        return rotationMode?.getNewRotation(editingAxis, shouldSnapRotation()) ?: 0.0
    }

}