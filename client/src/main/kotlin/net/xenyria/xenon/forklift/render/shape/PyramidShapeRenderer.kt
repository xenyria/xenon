package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.core.deltaOf
import net.xenyria.xenon.core.toDirection
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.shape.impl.PyramidShape
import org.joml.Matrix3f
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f

const val PYRAMID_LINE_THICKNESS = 4.0F

object PyramidShapeRenderer : IShapeRenderer<PyramidShape> {

    private data class PyramidCorners(val topLeft: Vector3dc, val topRight: Vector3dc, val bottomLeft: Vector3dc, val bottomRight: Vector3dc)

    private fun getCorners(shape: PyramidShape): PyramidCorners? {

        val direction = deltaOf(shape.position, shape.properties.apex)
        val length: Double = direction.length()
        if (length.isNaN() || length.isInfinite() || length == 0.0) return null

        direction.normalize()
        val (yaw, pitch) = toDirection(direction)

        val baseCenter = Vector3d(shape.position)
        val baseSize: Double = shape.properties.baseSize.toDouble()
        val xRad = Math.toRadians(pitch.toDouble()).toFloat()
        val yRad = Math.toRadians(180.0 + yaw.toDouble()).toFloat()
        val zRad = Math.toRadians(0.0).toFloat()

        val rotation = Matrix3f()
            .rotateZ(-zRad)
            .rotateY(-yRad)
            .rotateX(-xRad)

        val forward = Vector3f()
        val upwards = Vector3f()
        val sideways = Vector3f()

        rotation.getColumn(0, forward)
        rotation.getColumn(1, upwards)
        rotation.getColumn(2, sideways)

        val bottomLeft = Vector3d(baseCenter)
            .add(Vector3d(upwards).mul(-baseSize / 2))
            .add(Vector3d(forward).mul(-baseSize / 2))
        val bottomRight = Vector3d(baseCenter)
            .add(Vector3d(upwards).mul(-baseSize / 2))
            .add(Vector3d(forward).mul(baseSize / 2))
        val topLeft = Vector3d(baseCenter)
            .add(Vector3d(upwards).mul(baseSize / 2))
            .add(Vector3d(forward).mul(-baseSize / 2))
        val topRight = Vector3d(baseCenter)
            .add(Vector3d(upwards).mul(baseSize / 2))
            .add(Vector3d(forward).mul(baseSize / 2))

        return PyramidCorners(topLeft, topRight, bottomLeft, bottomRight)
    }

    override fun drawShape(renderer: IGameRenderer, shape: PyramidShape): Boolean {
        val box = Box(shape.position, shape.properties.apex).grow(0.25, 0.25, 0.25)
        if (!renderer.isInCameraFrustum(box)) return false

        val outlineColor = shape.properties.outlineColor
        val (topLeft, topRight, bottomLeft, bottomRight) = getCorners(shape) ?: return false

        renderer.drawPrimitives(listOf(LinePrimitive(Line(bottomLeft, bottomRight, PYRAMID_LINE_THICKNESS, outlineColor))), shape.properties.visibleThroughWalls)
        renderer.drawPrimitives(listOf(LinePrimitive(Line(bottomRight, topRight, PYRAMID_LINE_THICKNESS, outlineColor))), shape.properties.visibleThroughWalls)
        renderer.drawPrimitives(listOf(LinePrimitive(Line(topRight, topLeft, PYRAMID_LINE_THICKNESS, outlineColor))), shape.properties.visibleThroughWalls)
        renderer.drawPrimitives(listOf(LinePrimitive(Line(topLeft, bottomLeft, PYRAMID_LINE_THICKNESS, outlineColor))), shape.properties.visibleThroughWalls)

        for (corner in listOf(bottomLeft, bottomRight, topLeft, topRight)) {
            renderer.drawPrimitives(
                listOf(LinePrimitive(Line(shape.properties.apex, corner, PYRAMID_LINE_THICKNESS, outlineColor))),
                shape.properties.visibleThroughWalls
            )
        }
        return true
    }

}