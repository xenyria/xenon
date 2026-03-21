package net.xenyria.xenon.input.keyboard

import org.lwjgl.glfw.GLFW

/**
 * Helper functions & variables for keyboard, mouse and other input related things.
 */
const val ACTION_DOWN: Int = 1
const val ACTION_UP: Int = 0

fun isPressed(actionCode: Int): Boolean {
    return actionCode == GLFW.GLFW_PRESS
}

fun toNumberKey(keyCode: Int): Int? {
    if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) return keyCode - GLFW.GLFW_KEY_0
    return null
}

fun toKeyAction(actionCode: Int): KeyAction? {
    return KeyAction.fromCode(actionCode)
}

enum class KeyAction(val opCode: Int) {
    DOWN(ACTION_DOWN), UP(ACTION_UP);

    companion object {
        fun fromCode(code: Int): KeyAction? {
            return entries.find { it.opCode == code }
        }
    }
}
