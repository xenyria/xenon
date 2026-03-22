package net.xenyria.xenon.util

import net.xenyria.xenon.xenon
import org.joml.Matrix3x2fStack
import org.joml.Vector2f

const val HUD_OFFSET = 64
const val CHAT_OFFSET = -18

fun shift(matrixStack: Matrix3x2fStack, offset: Vector2f) {
    matrixStack.pushMatrix()
    matrixStack.translate(offset)
}

fun unshift(matrixStack: Matrix3x2fStack) {
    matrixStack.popMatrix()
}

fun getChatOffsetVector(): Vector2f {
    val verticalShift = getCurrentChatOffset()
    return Vector2f(0.0f, verticalShift.toFloat())
}

fun getHudOffsetVector(): Vector2f {
    val verticalShift = getCurrentHudOffset()
    return Vector2f(0.0f, verticalShift.toFloat())
}

fun getCurrentHudOffset(): Int {
    return if (xenon.shouldShiftHud()) HUD_OFFSET else 0
}

fun getCurrentChatOffset(): Int {
    return if (xenon.shouldShiftHud()) CHAT_OFFSET else 0
}