package net.xenyria.xenon.input.keyboard

import org.lwjgl.glfw.GLFW

/**
 * Helper functions & variables for keyboard, mouse and other input related things.
 */
const val ACTION_REPEAT = GLFW.GLFW_REPEAT
const val ACTION_DOWN: Int = GLFW.GLFW_PRESS
const val ACTION_UP: Int = GLFW.GLFW_RELEASE

fun toNumberKey(keyCode: Int): Int? {
    if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) return keyCode - GLFW.GLFW_KEY_0
    return null
}

fun toKeyAction(actionCode: Int): KeyAction? {
    return KeyAction.fromCode(actionCode)
}

enum class KeyAction(val opCode: Int) {
    DOWN(ACTION_DOWN),
    UP(ACTION_UP),
    REPEAT(ACTION_REPEAT);

    companion object {
        fun fromCode(code: Int): KeyAction? {
            return entries.find { it.opCode == code }
        }
    }
}
