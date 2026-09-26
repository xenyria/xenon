package net.xenyria.xenon.input.mouse

import com.mojang.blaze3d.platform.InputConstants
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent.MouseButton
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent.MouseButtonAction

fun fromSDL(button: Int, action: Int, mods: Int): MouseButtonEvent {
    val mouseButton = when (button) {
        InputConstants.MOUSE_BUTTON_LEFT -> MouseButton.LEFT
        InputConstants.MOUSE_BUTTON_MIDDLE -> MouseButton.MIDDLE
        InputConstants.MOUSE_BUTTON_RIGHT -> MouseButton.RIGHT
        else -> null
    }
    val mouseButtonAction = when (action) {
        InputConstants.PRESS -> MouseButtonAction.PRESS
        InputConstants.RELEASE -> MouseButtonAction.RELEASE
        else -> null
    }
    return MouseButtonEvent(mouseButton, mouseButtonAction)
}