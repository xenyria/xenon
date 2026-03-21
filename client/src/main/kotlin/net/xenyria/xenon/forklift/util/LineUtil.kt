package net.xenyria.xenon.forklift.util

import org.joml.Vector2d
import org.joml.Vector2dc

fun findClosestPointOnLine(firstPoint: Vector2dc, secondPoint: Vector2dc, cursor: Vector2dc): Vector2dc {
    val x0 = cursor.x()
    val y0 = cursor.y()
    val x1 = firstPoint.x()
    val y1 = firstPoint.y()
    val x2 = secondPoint.x()
    val y2 = secondPoint.y()

    // Check if the line is vertical
    if (x1 == x2) return Vector2d(x1, y0)

    // Calculate the slope (m) of the line
    val m = (y2 - y1) / (x2 - x1)

    // Calculate the y-intercept (b) of the line
    val b = y1 - m * x1

    // Calculate the x-coordinate of the closest point on the line
    val x = (m * (y0 - b) + x0) / (m * m + 1)

    // Calculate the y-coordinate of the closest point on the line
    val y = m * x + b

    return Vector2d(x, y)
}