package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.forklift.render.colorChannelIntToFactor
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import net.xenyria.xenon.forklift.render.shape.Line
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

class LinePrimitive(val line: Line) : IRenderPrimitive() {

    constructor(from: Vector3dc, to: Vector3dc, color: Color, width: Float = 1.0F) : this(
        Line(from, to, width, color)
    )

    override fun extractVertices(): List<Vertex> {
        val red = colorChannelIntToFactor(line.color.red)
        val green = colorChannelIntToFactor(line.color.green)
        val blue = colorChannelIntToFactor(line.color.blue)
        val alpha = colorChannelIntToFactor(line.color.alpha)

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