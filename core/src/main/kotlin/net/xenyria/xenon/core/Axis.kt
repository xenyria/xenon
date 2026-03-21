package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc

enum class Axis(positive: Vector3dc, negative: Vector3dc) {

    X(Vector3d(1.0, 0.0, 0.0), Vector3d(-1.0, 0.0, 0.0)),
    Y(Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, -1.0, 0.0)),
    Z(Vector3d(0.0, 0.0, 1.0), Vector3d(0.0, 0.0, -1.0));

    private val _positive: Vector3dc = positive
    private val _negative: Vector3dc = negative

    val positive: Vector3d get() = Vector3d(_positive)
    val negative: Vector3d get() = Vector3d(_negative)

}