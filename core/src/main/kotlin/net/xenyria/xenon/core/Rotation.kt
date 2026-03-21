package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.*

private const val PI_2 = Math.PI * 2 // Pi * 2

class Rotation(val yaw: Float, val pitch: Float) {
    companion object {
        val ZERO = Rotation(0.0F, 0.0F)
    }
}

// Converts yaw and pitch to a direction vector
fun calculateDirection(yaw: Number, pitch: Number): Vector3d {
    val rotX = yaw.toDouble()
    val rotY = pitch.toDouble()
    val x: Double
    val z: Double
    val y: Double = (-sin(Math.toRadians(rotY)))
    val xz = cos(Math.toRadians(rotY))
    x = (-xz * sin(Math.toRadians(rotX)))
    z = (xz * cos(Math.toRadians(rotX)))
    return Vector3d(x, y, z)
}

fun toDirection(vector: Vector3dc): Rotation {
    return toDirection(vector.x(), vector.y(), vector.z())
}

fun toDirection(vx: Double, vy: Double, vz: Double): Rotation {
    if (vx.isNaN() || vy.isNaN() || vz.isNaN()) return Rotation.ZERO
    var pitch: Float
    var yaw = 0f
    if (vx == 0.0 && vz == 0.0) {
        pitch = if (vy > 0.0) -90.0f else 90.0f
    } else {
        val theta = atan2(-vx, vz)
        yaw = Math.toDegrees((theta + PI_2) % PI_2).toFloat()
        val x2 = vx.pow(2.0)
        val z2 = vz.pow(2.0)
        val xz = sqrt(x2 + z2)
        pitch = Math.toDegrees(atan(-vy / xz)).toFloat()
    }
    return Rotation(yaw, pitch)
}