package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.forklift.render.colorToFloat
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import org.joml.Vector3dc
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

private const val PI = Math.PI.toFloat()

class SphereBuilder(
    private val offset: Vector3dc,
    color: Color,
    radius: Float, stacks: Int, slices: Int
) {

    private val _vertices: Array<Vertex>
    val vertices: List<Vertex> get() = _vertices.toList()

    private val red = colorToFloat(color.red)
    private val green = colorToFloat(color.green)
    private val blue = colorToFloat(color.blue)
    private val alpha = colorToFloat(color.alpha)

    private fun makeVertex(x: Number, y: Number, z: Number): Vertex {
        return Vertex(
            x.toDouble() + offset.x(),
            y.toDouble() + offset.y(),
            z.toDouble() + offset.z(),
            red, green, blue, alpha
        )
    }

    init {
        val numQuads = stacks * slices
        val vertices = ArrayList<Vertex>(numQuads)

        for (stack in 0 until stacks) {
            val phi1 = PI * (stack.toDouble() / stacks)
            val phi2 = PI * ((stack + 1).toDouble() / stacks)

            for (slice in 0 until slices) {
                val theta1 = 2.0 * PI * (slice.toDouble() / slices)
                val theta2 = 2.0 * PI * ((slice + 1).toDouble() / slices)

                vertices.add(
                    makeVertex(
                        (radius * sin(phi1) * cos(theta1)),
                        (radius * cos(phi1)),
                        (radius * sin(phi1) * sin(theta1))
                    )
                )
                vertices.add(
                    makeVertex(
                        (radius * sin(phi2) * cos(theta1)),
                        (radius * cos(phi2)),
                        (radius * sin(phi2) * sin(theta1))
                    )
                )
                vertices.add(
                    makeVertex(
                        (radius * sin(phi2) * cos(theta2)),
                        (radius * cos(phi2)),
                        (radius * sin(phi2) * sin(theta2))
                    )
                )
                vertices.add(
                    makeVertex(
                        (radius * sin(phi1) * cos(theta2)),
                        (radius * cos(phi1)),
                        (radius * sin(phi1) * sin(theta2))
                    )
                )
            }
        }
        _vertices = vertices.toTypedArray()
    }

}

class SpherePrimitive(val position: Vector3dc, val color: Color, val radius: Float, val stacks: Int, val slices: Int) : IRenderPrimitive() {
    override fun getVertices(): List<Vertex> {
        val builder = SphereBuilder(position, color, radius, stacks, slices)
        return builder.vertices
    }

    override fun getPipeline(visibleThroughWalls: Boolean): RenderPipelineType {
        return if (visibleThroughWalls) RenderPipelineType.SHAPES_THROUGH_WALLS else RenderPipelineType.SHAPES
    }
}