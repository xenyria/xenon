package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc

fun makeCenteredBox(position: Vector3dc, width: Double, height: Double): Box {
    val widthHalf = width / 2.0
    val heightHalf = height / 2.0
    return Box(
        position.x() - widthHalf,
        position.y() - heightHalf,
        position.z() - widthHalf,
        position.x() + widthHalf,
        position.y() + heightHalf,
        position.z() + widthHalf
    )
}

class Box {

    val dimensions: Vector3d get() = Vector3d(sizeX, sizeY, sizeZ)
    val origin: Vector3d get() = Vector3d(minX, minY, minZ)
    val sizeX: Double get() = (maxX - minX)
    val sizeY: Double get() = (maxY - minY)
    val sizeZ: Double get() = (maxZ - minZ)
    val minX: Double
    val minY: Double
    val minZ: Double
    val maxX: Double
    val maxY: Double
    val maxZ: Double

    constructor(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) {
        minX = minOf(x1, x2)
        minY = minOf(y1, y2)
        minZ = minOf(z1, z2)
        maxX = maxOf(x1, x2)
        maxY = maxOf(y1, y2)
        maxZ = maxOf(z1, z2)
    }

    constructor(from: Vector3dc, to: Vector3dc) : this(
        from.x(), from.y(), from.z(),
        to.x(), to.y(), to.z()
    )

    fun moveTo(x: Double, y: Double, z: Double): Box {
        return Box(x, y, z, x + sizeX, y + sizeY, z + sizeZ)
    }

    val from: Vector3d get() = Vector3d(minX, minY, minZ)
    val to: Vector3d get() = Vector3d(maxX, maxY, maxZ)
    val center: Vector3d get() = Vector3d((minX + maxX) / 2.0, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0)

    /**
     * Creates a new box that is expanded by the given amount in all directions.
     */
    fun grow(growX: Number, growY: Number, growZ: Number): Box {
        val minX = minX - growX.toDouble()
        val minY = minY - growY.toDouble()
        val minZ = minZ - growZ.toDouble()
        val maxX = maxX + growX.toDouble()
        val maxY = maxY + growY.toDouble()
        val maxZ = maxZ + growZ.toDouble()
        return Box(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun resizeTo(value: Vector3d): Box {
        return Box(minX, minY, minZ, minX + value.x, minY + value.y, minZ + value.z)
    }
}

