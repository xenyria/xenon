package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.shape.ShapeType

/**
 * Object that holds a registry of shape renderers for different shape types.
 */
object ShapeRendererRegistry {

    private val renderers: Map<ShapeType, IShapeRenderer<*>> = mapOf(
        ShapeType.BOX to BoxShapeRenderer,
        ShapeType.PYRAMID to PyramidShapeRenderer,
        ShapeType.POLYGON to PolygonShapeRenderer,
        ShapeType.PATH to PathShapeRenderer,
        ShapeType.SPHERE to SphereShapeRenderer,
    )

    /**
     * Retrieves the shape renderer for the specified shape type.
     * Using this for shape types that don't have a renderer assigned to them will throw an exception.
     */
    fun getRenderer(type: ShapeType): IShapeRenderer<*> {
        return requireNotNull(renderers[type]) { "No shape renderer available for $type" }
    }

}