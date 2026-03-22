package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.state.impl.ROTATION_GIZMO_RADIUS
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f

object GizmoRotationHelper {
    fun translateGizmoPosition(origin: Vector3dc, axis: Axis, rotation: Vector3dc): Vector3d {
        val matrix = Matrix4f()
        matrix.rotate(Math.toRadians(rotation.y()).toFloat(), 0f, 1f, 0f)
        matrix.rotate(Math.toRadians(rotation.x()).toFloat(), 1f, 0f, 0f)
        matrix.rotate(Math.toRadians(rotation.z()).toFloat(), 0f, 0f, 1f)

        val end = Vector3d(origin)
        val output = Vector3f()

        val offset = axis.positive
        matrix.translate(offset.x.toFloat(), offset.y.toFloat(), offset.z.toFloat())
        matrix.getTranslation(output)
        end.add(output.x.toDouble(), output.y.toDouble(), output.z.toDouble())
        return end
    }

    fun applyRotation(matrix: Matrix4f, rotation: Vector3dc) {
        matrix.rotate(Math.toRadians(rotation.y()).toFloat(), 0f, 1f, 0f)
        matrix.rotate(Math.toRadians(rotation.x()).toFloat(), 1f, 0f, 0f)
        matrix.rotate(Math.toRadians(rotation.z()).toFloat(), 0f, 0f, 1f)
    }

    fun transformPosition(rotation: Vector3dc, position: Vector3dc): Vector3d {
        val matrix = Matrix4f()
        applyRotation(matrix, rotation)

        val projected = Vector3f(ROTATION_GIZMO_RADIUS.toFloat(), 0.0f, 0.0f)
        matrix.transformPosition(
            Vector3f(
                position.x().toFloat(),
                position.y().toFloat(),
                position.z().toFloat()
            ), projected
        )
        return Vector3d(projected.x.toDouble(), projected.y.toDouble(), projected.z.toDouble())
    }
}