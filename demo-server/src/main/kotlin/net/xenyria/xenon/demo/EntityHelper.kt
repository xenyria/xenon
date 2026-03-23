package net.xenyria.xenon.demo

import org.bukkit.entity.Display
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3d

fun applyRotation(entity: Display, rotation: Vector3d) {
    val quatX = Quaternionf().rotateX(Math.toRadians(rotation.x).toFloat())
        .rotateZ(Math.toRadians(rotation.z).toFloat())
    val quatY = Quaternionf().rotateY(Math.toRadians(rotation.y).toFloat())
    quatY.mul(quatX)

    val matrix = Matrix4f()
    matrix.identity()
    matrix.rotate(quatY)

    val leftQuaternion = Quaternionf()

    leftQuaternion.set(quatX)
    leftQuaternion.set(quatY)

    val transformation = entity.transformation
    transformation.leftRotation.set(leftQuaternion)
    entity.transformation = transformation
}