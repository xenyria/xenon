package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.core.Axis
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color
import kotlin.math.roundToInt
import kotlin.math.sin

const val MAX_COLOR_VALUE = 255.0f
const val MAX_COLOR_CHANNEL_VALUE = 255

fun roundToNearestMultiple(vector: Vector3dc, multiple: Double): Vector3d {
    val x = (vector.x() / multiple).roundToInt() * multiple
    val y = (vector.y() / multiple).roundToInt() * multiple
    val z = (vector.z() / multiple).roundToInt() * multiple
    return Vector3d(x, y, z)
}

fun roundToNearestMultiple(vector: Vector3dc, multiple: Double, axis: Axis): Vector3d {
    var x = vector.x()
    var y = vector.y()
    var z = vector.z()
    when (axis) {
        Axis.X -> x = (vector.x() / multiple).roundToInt() * multiple
        Axis.Y -> y = (vector.y() / multiple).roundToInt() * multiple
        Axis.Z -> z = (vector.z() / multiple).roundToInt() * multiple
    }
    return Vector3d(x, y, z)
}

/**
 * Returns a value between 0 and 1 that oscillates between 0 and 1. The period is the time in milliseconds for one
 * full cycle. The min value is the minimum value that is returned.
 */
fun sinModifier(period: Number, min: Number): Double {
    val time = System.currentTimeMillis()
    return min.toDouble() + sin(time / period.toDouble()) * (1 - min.toDouble())
}

/**
 * Converts the given color to a float value between 0 and 1.
 */
fun colorToFloat(value: Int): Float {
    return (value / MAX_COLOR_VALUE).coerceIn(0.0F, 1.0F)
}

/**
 * Multiplies the given color by the given modifier. Assures that the resulting color is still valid. (all channels
 * are between 0 and 255)
 */
fun multiplyColor(color: Color, mod: Double): Color {
    var newRed = (color.red * mod).toInt()
    var newGreen = (color.green * mod).toInt()
    var newBlue = (color.blue * mod).toInt()
    newRed = newRed.coerceIn(0, MAX_COLOR_CHANNEL_VALUE)
    newGreen = newGreen.coerceIn(0, MAX_COLOR_CHANNEL_VALUE)
    newBlue = newBlue.coerceIn(0, MAX_COLOR_CHANNEL_VALUE)
    return Color(newRed, newGreen, newBlue)
}