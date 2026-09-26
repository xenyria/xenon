package net.xenyria.xenon.input.keyboard

import net.minecraft.client.Minecraft
import net.minecraft.client.input.KeyEvent

/**
 * Provides utility functions to simulate key presses.
 */
class KeyboardSimulator(
    private val windowId: Long,
) {
    fun simulateKeyPress(action: Int, keyEvent: KeyEvent) {
        Minecraft.getInstance().keyboardHandler.keyPress(windowId, action, keyEvent)
    }

    fun simulateKeyRelease(keyCode: Int, scanCode: Int) {
        simulateKeyPress(ACTION_UP, KeyEvent(keyCode, scanCode, 123))
    }
}