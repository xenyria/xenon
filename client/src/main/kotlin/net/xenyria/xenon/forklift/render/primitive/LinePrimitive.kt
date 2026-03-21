package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.forklift.render.colorToFloat
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import net.xenyria.xenon.forklift.render.shape.Line
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

class LinePrimitive(val line: Line) : IRenderPrimitive() {

    constructor(from: Vector3dc, to: Vector3dc, color: Color, width: Float = 1.0F) : this(
        Line(from, to, width, color)
    )

    override fun getVertices(): List<Vertex> {
        val red = colorToFloat(line.color.red)
        val green = colorToFloat(line.color.green)
        val blue = colorToFloat(line.color.blue)
        val alpha = colorToFloat(line.color.alpha)

        val computedNormal = (Vector3d(line.to).sub(line.from)).normalize()
        return listOf(
            Vertex(line.from.x(), line.from.y(), line.from.z(), red, green, blue, alpha).apply {
                lineWidth = line.width
                normal = computedNormal
            },
            Vertex(line.to.x(), line.to.y(), line.to.z(), red, green, blue, alpha).apply {
                lineWidth = line.width
                normal = computedNormal
            }
        )
    }

    override fun getPipeline(visibleThroughWalls: Boolean): RenderPipelineType {
        return if (visibleThroughWalls) RenderPipelineType.LINES_THROUGH_WALLS else RenderPipelineType.LINES
    }
}