package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.forklift.util.Catmull
import net.xenyria.xenon.shape.impl.PathShape
import org.joml.Vector3dc
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

const val PATH_LINE_WIDTH = 4.0F

private data class InterpolatableSegment(
    val previous: Vector3dc,
    val from: Vector3dc,
    val to: Vector3dc,
    val next: Vector3dc
)

object PathShapeRenderer : IShapeRenderer<PathShape> {

    private fun getSegments(points: List<Vector3dc>, open: Boolean): List<Pair<Vector3dc, Vector3dc>> {
        val segments = ArrayList<Pair<Vector3dc, Vector3dc>>()
        for ((index, point) in points.withIndex()) {
            val toIndex = (index + 1) % points.size
            val from = point
            val to = points[toIndex]

            if (open && toIndex == 0) continue
            segments.add(from to to)
        }
        return segments
    }

    private fun toInterpolatableSegments(segments: List<Pair<Vector3dc, Vector3dc>>, open: Boolean): List<InterpolatableSegment> {
        val output = ArrayList<InterpolatableSegment>()

        fun getSegment(index: Int, wrapAround: Boolean): Pair<Vector3dc, Vector3dc>? {
            var index = index
            if (!wrapAround) {
                if (index < 0 || index > segments.size - 1) return null
            }
            if (index < 0) index = segments.size - 1
            if (index > segments.size - 1) index = 0
            return segments[index]
        }

        val wrapAround = !open
        for ((index, from) in segments.withIndex()) {
            val previous = getSegment(index - 1, wrapAround)
            val to = getSegment(index + 1, wrapAround)

            output.add(
                InterpolatableSegment(
                    previous?.first ?: from.first,
                    from.first,
                    from.second,
                    to?.second ?: from.second,
                )
            )
        }
        return output
    }

    override fun drawShape(renderer: IGameRenderer, shape: PathShape): Boolean {
        runCatching {
            val lines = ArrayList<LinePrimitive>()
            val segments = getSegments(shape.properties.points, shape.properties.isOpen)
            for (segment in toInterpolatableSegments(segments, shape.properties.isOpen)) {
                val box = Box(segment.from, segment.to)
                if (!renderer.isInCameraFrustum(box)) continue

                val distance = segment.from.distance(segment.to)
                val splitCount = min(8, max((distance * 16).toInt(), 32))

                if (shape.properties.isSmooth) {
                    lines.addAll(
                        generatePrimitives(
                            Catmull.interpolateSegment(
                                splitCount, true,
                                segment.previous, segment.from, segment.to, segment.next
                            ), shape.properties.color
                        )
                    )
                } else {
                    lines.addAll(
                        generatePrimitives(listOf(segment.from, segment.to), shape.properties.color)
                    )
                }
            }
            renderer.drawPrimitives(lines, shape.properties.visibleThroughWalls)
        }
        return true
    }

    private fun generatePrimitives(
        points: List<Vector3dc>,
        color: Color
    ): List<LinePrimitive> {
        val primitives = ArrayList<LinePrimitive>()
        for ((index, point) in points.withIndex()) {
            val nextIndex = index + 1
            if (nextIndex > points.size - 1) continue

            primitives.add(LinePrimitive(point, points[nextIndex], color, PATH_LINE_WIDTH))
        }
        return primitives
    }
}