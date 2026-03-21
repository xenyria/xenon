package net.xenyria.xenon.input.keyboard

import net.minecraft.client.input.KeyEvent
import net.xenyria.xenon.mixin.KeyboardHandlerInvoker

/**
 * Provides utility functions to simulate key presses.
 */
class KeyboardSimulator(windowId: Long, mixin: KeyboardHandlerInvoker) {

    private val _mixin: KeyboardHandlerInvoker = mixin
    private val _windowId: Long = windowId

    fun simulateKeyPress(action: Int, keyEvent: KeyEvent) {
        _mixin.callKeyPress(_windowId, action, keyEvent)
    }

    fun simulateKeyRelease(keyCode: Int, scanCode: Int) {
        simulateKeyPress(ACTION_UP, KeyEvent(keyCode, scanCode, 0))
    }
}