package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.forklift.render.colorToFloat
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import java.awt.Color

class BoxPrimitive(
    var box: Box,
    val color: Color,
    var rotation: Vector3dc = Vector3d(0.0)
) : IRenderPrimitive() {
    private fun makeVertex(matrix: Matrix4f, x: Double, y: Double, z: Double, color: Color): Vertex {
        val transformed = matrix.transformPosition(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()))

        return Vertex(
            transformed.x + box.minX + (box.sizeX / 2.0),
            transformed.y + box.minY + (box.sizeY / 2.0),
            transformed.z + box.minZ + (box.sizeZ / 2.0),
            colorToFloat(color.red),
            colorToFloat(color.green),
            colorToFloat(color.blue),
            colorToFloat(color.alpha)
        )
    }

    override fun getVertices(): List<Vertex> {
        val vertices = ArrayList<Vertex>()

        val matrix = Matrix4f()

        val minX = 0.0
        val minY = 0.0
        val minZ = 0.0
        val maxX = box.sizeX
        val maxY = box.sizeY
        val maxZ = box.sizeZ
        net.xenyria.xenon.forklift.editor.GizmoRotationHelper.applyRotation(matrix, rotation)
        matrix.translate(-(box.sizeX / 2.0F).toFloat(), -(box.sizeY / 2.0F).toFloat(), -(box.sizeZ / 2.0F).toFloat())

        // Front Face
        vertices.add(makeVertex(matrix, minX, minY, maxZ, color))
        vertices.add(makeVertex(matrix, maxX, minY, maxZ, color))
        vertices.add(makeVertex(matrix, maxX, maxY, maxZ, color))
        vertices.add(makeVertex(matrix, minX, maxY, maxZ, color))

        // Back face
        vertices.add(makeVertex(matrix, maxX, minY, minZ, color))
        vertices.add(makeVertex(matrix, minX, minY, minZ, color))
        vertices.add(makeVertex(matrix, minX, maxY, minZ, color))
        vertices.add(makeVertex(matrix, maxX, maxY, minZ, color))

        // Left face
        vertices.add(makeVertex(matrix, minX, minY, minZ, color))
        vertices.add(makeVertex(matrix, minX, minY, maxZ, color))
        vertices.add(makeVertex(matrix, minX, maxY, maxZ, color))
        vertices.add(makeVertex(matrix, minX, maxY, minZ, color))

        // Right face
        vertices.add(makeVertex(matrix, maxX, minY, maxZ, color))
        vertices.add(makeVertex(matrix, maxX, minY, minZ, color))
        vertices.add(makeVertex(matrix, maxX, maxY, minZ, color))
        vertices.add(makeVertex(matrix, maxX, maxY, maxZ, color))

        // Top face
        vertices.add(makeVertex(matrix, minX, maxY, maxZ, color))
        vertices.add(makeVertex(matrix, maxX, maxY, maxZ, color))
        vertices.add(makeVertex(matrix, maxX, maxY, minZ, color))
        vertices.add(makeVertex(matrix, minX, maxY, minZ, color))

        // Bottom face
        vertices.add(makeVertex(matrix, minX, minY, minZ, color))
        vertices.add(makeVertex(matrix, maxX, minY, minZ, color))
        vertices.add(makeVertex(matrix, maxX, minY, maxZ, color))
        vertices.add(makeVertex(matrix, minX, minY, maxZ, color))

        return vertices
    }

    override fun getPipeline(visibleThroughWalls: Boolean): RenderPipelineType {
        return if (visibleThroughWalls) RenderPipelineType.SHAPES_THROUGH_WALLS else RenderPipelineType.SHAPES
    }
}