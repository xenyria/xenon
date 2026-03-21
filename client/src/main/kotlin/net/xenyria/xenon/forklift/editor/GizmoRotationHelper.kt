package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.core.Axis
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
}