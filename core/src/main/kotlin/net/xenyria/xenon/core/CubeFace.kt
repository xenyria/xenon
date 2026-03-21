package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc

enum class CubeFace(private val _normal: Vector3dc) {
    NORTH(Vector3d(0.0, 0.0, -1.0)),
    EAST(Vector3d(1.0, 0.0, 0.0)),
    SOUTH(Vector3d(0.0, 0.0, 1.0)),
    WEST(Vector3d(-1.0, 0.0, 0.0)),
    UP(Vector3d(0.0, 1.0, 0.0)),
    DOWN(Vector3d(0.0, -1.0, 0.0));

    val normal: Vector3d
        get() = Vector3d(_normal)
}