package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

val AXIS_X_COLOR = Color(255, 64, 64)
val AXIS_Y_COLOR = Color(64, 255, 64)
val AXIS_Z_COLOR = Color(64, 64, 255)

const val AXIS_EDIT_LENGTH = 1.0

private val AXIS_COLORS = mapOf(
    Axis.X to AXIS_X_COLOR,
    Axis.Y to AXIS_Y_COLOR,
    Axis.Z to AXIS_Z_COLOR
)

fun getAxisColor(axis: Axis): Color {
    return requireNotNull(AXIS_COLORS[axis]) { "Encountered unknown axis $axis" }
}

enum class Axis(positive: Vector3dc, negative: Vector3dc) {

    X(Vector3d(1.0, 0.0, 0.0), Vector3d(-1.0, 0.0, 0.0)),
    Y(Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, -1.0, 0.0)),
    Z(Vector3d(0.0, 0.0, 1.0), Vector3d(0.0, 0.0, -1.0));

    private val _positive: Vector3dc = positive
    private val _negative: Vector3dc = negative

    val positive: Vector3d get() = Vector3d(_positive)
    val negative: Vector3d get() = Vector3d(_negative)

}