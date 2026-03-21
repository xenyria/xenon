package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.floor

val THREE_DECIMALS = DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.US))
val TWO_DECIMALS = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US))
val ONE_DECIMAL = DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US))
val ZERO_DECIMALS = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.US))

val ZERO: Vector3dc = Vector3d(0.0, 0.0, 0.0)

fun getVectorComponent(axis: Axis, vector: Vector3dc): Double {
    return when (axis) {
        Axis.X -> vector.x()
        Axis.Y -> vector.y()
        Axis.Z -> vector.z()
    }
}

fun Number.format(decimals: Int): String {
    val format = when (decimals) {
        0 -> ZERO_DECIMALS
        1 -> ONE_DECIMAL
        2 -> TWO_DECIMALS
        3 -> THREE_DECIMALS
        else -> DecimalFormat("0." + "0".repeat(decimals))
    }
    return format.format(this)
}

fun directionOf(start: Vector3dc, end: Vector3dc): Vector3d {
    return deltaOf(start, end).normalize()
}

fun deltaOf(start: Vector3dc, end: Vector3dc): Vector3d {
    val destination = Vector3d()
    end.sub(start, destination)
    return destination
}

fun validateVector(vector: Vector3dc): Boolean {
    return !vector.x().isInfinite() && !vector.x().isNaN()
            && !vector.y().isInfinite() && !vector.y().isNaN()
            && !vector.z().isInfinite() && !vector.z().isNaN()
}

fun toBlockCoordinate(position: Number): Int {
    return (floor(position.toDouble())).toInt()
}

fun normalizeZeros(vector: Vector3dc): Vector3d {
    var x = vector.x()
    var y = vector.y()
    var z = vector.z()

    if (x == -0.0) x = 0.0
    if (y == -0.0) y = 0.0
    if (z == -0.0) z = 0.0
    return Vector3d(x, y, z)
}