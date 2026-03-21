package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.core.Rotation
import net.xenyria.xenon.core.toDirection
import net.xenyria.xenon.forklift.render.colorToFloat
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import org.joml.Math.toRadians
import org.joml.Matrix4d
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

const val SEGMENTS = 8

private object ConeBakery {

    private val center = Vector3d(0.0)

    private fun getSegmentPoints(rotation: Double, step: Double, radius: Double): Pair<Vector3d, Vector3d> {
        val fromRotationMatrix = Matrix4d()
        val toRotationMatrix = Matrix4d()
        fromRotationMatrix.rotate(toRadians(rotation), 0.0, 1.0, 0.0)
        toRotationMatrix.rotate(toRadians(rotation + step), 0.0, 1.0, 0.0)

        val from = fromRotationMatrix.transformPosition(Vector3d(radius, 0.0, 0.0))
        val to = toRotationMatrix.transformPosition(Vector3d(radius, 0.0, 0.0))
        return from to to
    }

    fun makeVertices(
        segments: Int, offset: Vector3d,
        radius: Double, length: Double,
        orientation: Rotation, color: Color
    ): List<Vertex> {
        val vertices = ArrayList<Vertex>()

        // Bottom surface
        val top = Vector3d(0.0, length, 0.0)

        // Vertices on the base
        val step = 360F / segments
        var currentRotation = 0f
        while (currentRotation <= 360f) {

            val (firstPoint, secondPoint) = getSegmentPoints(currentRotation.toDouble(), step.toDouble(), radius)

            // Baseplate
            vertices.add(makeVertex(offset, firstPoint, orientation, color))
            vertices.add(makeVertex(offset, secondPoint, orientation, color))
            vertices.add(makeVertex(offset, center, orientation, color))
            vertices.add(makeVertex(offset, secondPoint, orientation, color))

            // Top part of the cone
            vertices.add(makeVertex(offset, firstPoint, orientation, color))
            vertices.add(makeVertex(offset, secondPoint, orientation, color))
            vertices.add(makeVertex(offset, top, orientation, color))
            vertices.add(makeVertex(offset, top, orientation, color))

            currentRotation += step
        }
        return vertices
    }

    fun makeVertex(offset: Vector3d, position: Vector3d, rotation: Rotation, color: Color): Vertex {
        val matrix = Matrix4d()
        matrix.translate(offset)
        matrix.rotate(toRadians(-rotation.yaw).toDouble(), 0.0, 1.0, 0.0)
        matrix.rotate(toRadians(rotation.pitch.toDouble() + 90.0), 1.0, 0.0, 0.0)
        matrix.translate(position.x, position.y, position.z)

        val position = matrix.transformPosition(Vector3d())

        return Vertex(
            position.x, position.y, position.z,
            colorToFloat(color.red),
            colorToFloat(color.green),
            colorToFloat(color.blue),
            colorToFloat(color.alpha)
        )
    }
}

class ConePrimitive(val top: Vector3dc, val base: Vector3dc, val color: Color, val baseRadius: Double) : IRenderPrimitive() {

    override fun getVertices(): List<Vertex> {
        var direction = Vector3d(top).sub(base)
        val length = direction.length()

        direction = direction.normalize()
        val rotation = toDirection(direction)

        return ConeBakery.makeVertices(
            SEGMENTS, Vector3d(base.x(), base.y(), base.z()), baseRadius, length,
            rotation, color,
        )
    }

    override fun getPipeline(visibleThroughWalls: Boolean): RenderPipelineType {
        return if (visibleThroughWalls) RenderPipelineType.SHAPES_THROUGH_WALLS else RenderPipelineType.SHAPES
    }
}