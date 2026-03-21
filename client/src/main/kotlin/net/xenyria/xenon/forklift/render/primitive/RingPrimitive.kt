package net.xenyria.xenon.forklift.render.primitive

import net.xenyria.xenon.forklift.editor.state.GizmoRotationHelper.getCircleVectors
import net.xenyria.xenon.forklift.render.shape.Line
import org.joml.Vector3dc
import java.awt.Color

fun makeRingPrimitive(center: Vector3dc, radius: Double, color: Color, yaw: Float, width: Float, horizontal: Boolean): List<LinePrimitive> {
    val points = ArrayList<Vector3dc>()
    for (point in getCircleVectors(center, radius, yaw, horizontal, 5.0)) {
        points.add(point.vector)
    }

    val lines = ArrayList<Line>()
    for (index in points.indices) {
        val from = points[index]
        val to = points[(index + 1) % points.size]
        lines.add(Line(from, to, width, color))
    }

    return lines.map { LinePrimitive(it) }
}