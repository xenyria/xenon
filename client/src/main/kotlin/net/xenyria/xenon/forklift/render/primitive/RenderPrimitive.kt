package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.forklift.render.colorChannelIntToFactor
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

/**
 * Represents a single vertex in 3D space with RGBA color information.
 */
data class Vertex(
    val x: Double, val y: Double, val z: Double,
    val red: Float, val green: Float, val blue: Float, val alpha: Float
) {

    val position: Vector3d get() = Vector3d(x, y, z)
    var normal: Vector3dc? = null
    var lineWidth: Float? = null

    companion object {
        /**
         * Helper function for creating a vertex from a position and Java AWT color.
         */
        fun makeVertex(x: Double, y: Double, z: Double, color: Color): Vertex {
            return Vertex(
                x, y, z,
                colorChannelIntToFactor(color.red),
                colorChannelIntToFactor(color.green),
                colorChannelIntToFactor(color.blue),
                colorChannelIntToFactor(color.alpha)
            )
        }
    }
}

/**
 * Abstract class representing a renderable primitive in 3D space.
 */
abstract class IRenderPrimitive {

    /**
     * Extracts the vertices required to render this primitive.
     */
    abstract fun extractVertices(): List<Vertex>

    /**
     * Returns the pipeline type that should be used to render this primitive.
     */
    abstract fun getPipeline(visibleThroughWalls: Boolean): RenderPipelineType
}
