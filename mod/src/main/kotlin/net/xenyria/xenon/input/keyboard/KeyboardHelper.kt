package net.xenyria.xenon.input.keyboard

import com.mojang.blaze3d.platform.InputConstants


/**
 * Helper functions & variables for keyboard, mouse and other input related things.
 */
const val ACTION_REPEAT = InputConstants.REPEAT
const val ACTION_DOWN: Int = InputConstants.PRESS
const val ACTION_UP: Int = InputConstants.RELEASE

private val numberKeyMapping = mapOf(
    InputConstants.KEY_0 to 0,
    InputConstants.KEY_1 to 1,
    InputConstants.KEY_2 to 2,
    InputConstants.KEY_3 to 3,
    InputConstants.KEY_4 to 4,
    InputConstants.KEY_5 to 5,
    InputConstants.KEY_6 to 6,
    InputConstants.KEY_7 to 7,
    InputConstants.KEY_8 to 8,
    InputConstants.KEY_9 to 9
)

fun toNumberKey(keyCode: Int): Int? {
    return numberKeyMapping[keyCode]
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
