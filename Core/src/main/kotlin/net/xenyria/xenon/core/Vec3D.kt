/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.core

import java.util.*

interface IVec3D {
    val x: Double
    val y: Double
    val z: Double

    operator fun get(axis: Axis): Double {
        return when (axis) {
            Axis.X -> x
            Axis.Y -> y
            Axis.Z -> z
        }
    }

    operator fun plus(other: IVec3D): IVec3D {
        return Vec3D(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: IVec3D): IVec3D {
        return Vec3D(x - other.x, y - other.y, z - other.z)
    }

    operator fun times(scalar: Double): IVec3D {
        return Vec3D(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Double): IVec3D {
        return Vec3D(x / scalar, y / scalar, z / scalar)
    }

}

class Vec3D(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : IVec3D {
    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec3D

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }
}

val ZERO = Vec3D(0.0, 0.0, 0.0)