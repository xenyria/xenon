package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.forklift.render.colorToFloat
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

data class Vertex(
    val x: Double, val y: Double, val z: Double,
    val red: Float, val green: Float, val blue: Float, val alpha: Float
) {

    val position: Vector3d get() = Vector3d(x, y, z)
    var normal: Vector3dc? = null
    var lineWidth: Float? = null

    companion object {
        fun makeVertex(x: Double, y: Double, z: Double, color: Color): Vertex {
            return Vertex(
                x, y, z,
                colorToFloat(color.red),
                colorToFloat(color.green),
                colorToFloat(color.blue),
                colorToFloat(color.alpha)
            )
        }
    }
}

abstract class IRenderPrimitive {
    abstract fun getVertices(): List<Vertex>
    abstract fun getPipeline(visibleThroughWalls: Boolean): RenderPipelineType
}
