package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.core.makeCenteredBox
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.forklift.render.primitive.BoxPrimitive
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.shape.impl.BoxShape
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color
import kotlin.math.max

const val LINE_WIDTH = 4.0F

object BoxShapeRenderer : IShapeRenderer<BoxShape> {

    private fun isVisible(renderer: IGameRenderer, shape: BoxShape): Boolean {
        val boxCenter = Vector3d(shape.position).add(Vector3d(shape.properties.dimensions).mul(0.5))
        val box = makeCenteredBox(
            Vector3d(boxCenter.x, boxCenter.y, boxCenter.z),
            max(shape.properties.dimensions.x(), shape.properties.dimensions.z()),
            shape.properties.dimensions.y()
        )
        return renderer.isInCameraFrustum(box)
    }

    private fun renderOutline(renderer: IGameRenderer, shape: BoxShape) {
        val min = shape.position
        val max = Vector3d(shape.position).add(Vector3d(shape.properties.dimensions))
// Draw lines
        val lines = ArrayList<LinePrimitive>()
        lines.addAll(getSurface(min, max, shape.properties.outlineColor, min.y(), LINE_WIDTH))
        lines.addAll(getSurface(min, max, shape.properties.outlineColor, max.y, LINE_WIDTH))
        lines.addAll(getCorners(min, max, shape.properties.outlineColor, LINE_WIDTH))
        renderer.drawPrimitives(lines, shape.properties.visibleThroughWalls)
    }

    override fun drawShape(renderer: IGameRenderer, shape: BoxShape) {
        if (!isVisible(renderer, shape)) return

        if (shape.properties.onlyRenderOutline) {
            renderOutline(renderer, shape)
        } else {
            renderOutline(renderer, shape)
            renderer.drawPrimitives(
                listOf(BoxPrimitive(shape.box, shape.properties.boxColor)), shape.properties.visibleThroughWalls
            )
        }
    }

    private fun getCorners(
        min: Vector3dc, max: Vector3dc, outlineColor: Color,
        lineWidth: Float
    ): List<LinePrimitive> {
        val lines = ArrayList<LinePrimitive>()
        run {
            val start = Vector3d(min.x(), min.y(), min.z())
            val end = Vector3d(min.x(), max.y(), min.z())
            lines.add(LinePrimitive(start, end, outlineColor, lineWidth))
        }
        run {
            val start = Vector3d(max.x(), min.y(), min.z())
            val end = Vector3d(start.x(), max.y(), start.z())
            lines.add(LinePrimitive(start, end, outlineColor, lineWidth))
        }
        run {
            val start = Vector3d(max.x(), min.y(), max.z())
            val end = Vector3d(start.x(), max.y(), start.z())
            lines.add(LinePrimitive(start, end, outlineColor, lineWidth))
        }
        run {
            val start = Vector3d(min.x(), min.y(), max.z())
            val end = Vector3d(start.x(), max.y(), start.z())
            lines.add(LinePrimitive(start, end, outlineColor, lineWidth))
        }
        return lines
    }

    private fun getSurface(
        min: Vector3dc, max: Vector3dc,
        outlineColor: Color, yToUse: Double,
        lineWidth: Float
    ): List<LinePrimitive> {
        val lines = ArrayList<LinePrimitive>()
        lines.add(
            LinePrimitive(
                Line(
                    Vector3d(min.x(), yToUse, min.z()),
                    Vector3d(max.x(), yToUse, min.z()), lineWidth, outlineColor
                )
            )
        )
        lines.add(
            LinePrimitive(
                Line(
                    Vector3d(min.x(), yToUse, min.z()),
                    Vector3d(min.x(), yToUse, max.z()), lineWidth, outlineColor
                )
            )
        )
        lines.add(
            LinePrimitive(
                Line(
                    Vector3d(max.x(), yToUse, min.z()),
                    Vector3d(max.x(), yToUse, max.z()), lineWidth, outlineColor
                )
            )
        )
        lines.add(
            LinePrimitive(
                Line(
                    Vector3d(min.x(), yToUse, max.z()),
                    Vector3d(max.x(), yToUse, max.z()), lineWidth, outlineColor
                )
            )
        )
        return lines
    }


}