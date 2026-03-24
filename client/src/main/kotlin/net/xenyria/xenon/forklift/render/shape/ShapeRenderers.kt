package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.shape.ShapeType

object ShapeRenderers {

    private val _renderers: Map<ShapeType, IShapeRenderer<*>> = mapOf(
        ShapeType.BOX to BoxShapeRenderer,
        ShapeType.PYRAMID to PyramidShapeRenderer,
        ShapeType.POLYGON to PolygonShapeRenderer,
        ShapeType.PATH to PathShapeRenderer,
        ShapeType.SPHERE to SphereShapeRenderer,
    )

    fun getRenderer(type: ShapeType): IShapeRenderer<*> {
        return requireNotNull(_renderers[type]) { "No shape renderer available for $type" }
    }

}