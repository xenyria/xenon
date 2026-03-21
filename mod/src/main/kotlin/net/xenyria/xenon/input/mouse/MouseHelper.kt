package net.xenyria.xenon.input.mouse

import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent.MouseButton
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent.MouseButtonAction

fun fromLWJGL(button: Int, action: Int, mods: Int): MouseButtonEvent {
    val mouseButton = when (button) {
        0 -> MouseButton.LEFT
        1 -> MouseButton.RIGHT
        2 -> MouseButton.MIDDLE
        else -> null
    }
    val mouseButtonAction = when (action) {
        0 -> MouseButtonAction.RELEASE
        1 -> MouseButtonAction.PRESS
        else -> null
    }
    return MouseButtonEvent(mouseButton, mouseButtonAction)
}