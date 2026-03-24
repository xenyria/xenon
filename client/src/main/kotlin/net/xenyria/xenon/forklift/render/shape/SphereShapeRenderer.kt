package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.core.makeCenteredBox
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.forklift.render.primitive.SphereBuilder
import net.xenyria.xenon.forklift.render.primitive.SpherePrimitive
import net.xenyria.xenon.shape.impl.SphereShape

object SphereShapeRenderer : IShapeRenderer<SphereShape> {

    override fun drawShape(renderer: IGameRenderer, shape: SphereShape): Boolean {
        val box = makeCenteredBox(shape.position, shape.properties.radius * 2.0, shape.properties.radius * 2.0)
        if (!renderer.isInCameraFrustum(box)) return false

        if (shape.properties.isOutline) {
            val builder = SphereBuilder(
                shape.position, shape.properties.color, shape.properties.radius,
                shape.properties.stacks ?: 8,
                shape.properties.slices ?: 8,
            )
            val vertices = builder.vertices

            var index = 0
            while (index < vertices.size) {
                val v0 = vertices[index]
                val v1 = vertices[index + 1]
                val v2 = vertices[index + 2]
                val v3 = vertices[index + 3]

                renderer.drawPrimitives(
                    listOf(
                        LinePrimitive(Line(v0.position, v1.position, 4.0F, shape.properties.color)),
                        LinePrimitive(Line(v1.position, v2.position, 4.0F, shape.properties.color)),
                        LinePrimitive(Line(v2.position, v3.position, 4.0F, shape.properties.color)),
                        LinePrimitive(Line(v3.position, v0.position, 4.0F, shape.properties.color)),
                    ),
                    shape.properties.visibleThroughWalls
                )

                index += 4
            }
        } else {
            renderer.drawPrimitives(
                listOf(
                    SpherePrimitive(
                        shape.position,
                        shape.properties.color,
                        shape.properties.radius,
                        shape.properties.stacks ?: 8,
                        shape.properties.slices ?: 8,
                    )
                ),
                shape.properties.visibleThroughWalls
            )
        }
        return true
    }
}