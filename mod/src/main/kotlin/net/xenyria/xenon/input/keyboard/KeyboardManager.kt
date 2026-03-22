package net.xenyria.xenon.input.keyboard

import net.minecraft.client.Minecraft
import net.minecraft.client.input.KeyEvent
import net.xenyria.xenon.game
import net.xenyria.xenon.mixin.KeyboardHandlerInvoker
import net.xenyria.xenon.xenon
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

private data class HeldKey(val keyCode: Int, val scanCode: Int)

class KeyboardManager(windowId: Long, invoker: KeyboardHandlerInvoker) {

    private val keyboardInvoker: KeyboardHandlerInvoker = invoker
    val simulator = KeyboardSimulator(windowId, keyboardInvoker)

    private val _heldKeys = HashMap<Int, HeldKey>()

    @Synchronized
    fun releaseAllKeys() {
        val keysToRelease = _heldKeys
        for (key in keysToRelease.values) {
            simulator.simulateKeyRelease(key.keyCode, key.scanCode)
        }
        _heldKeys.clear()
    }

    fun onKeyPress(window: Long, action: KeyAction, keyEvent: KeyEvent, callbackInfo: CallbackInfo) {
        if (window != Minecraft.getInstance().window.handle()) return
        if (action == KeyAction.DOWN) {
            if (game.gui.chat.isChatFocused) return
            handleKeyPress(keyEvent.key)
        }

        val forklift = xenon.getForkliftOrNull()
        if (forklift != null && forklift.editor.isMouseLocked()) {
            if (action != KeyAction.UP) {
                callbackInfo.cancel()
                return
            }
        }

        synchronized(this) {
            if (action == KeyAction.DOWN) {
                _heldKeys[keyEvent.key] = HeldKey(keyEvent.key, keyEvent.scancode)
            } else if (action == KeyAction.UP) {
                _heldKeys.remove(keyEvent.key)
            }
        }
    }

    private fun handleKeyPress(key: Int) {
        val numberKey = toNumberKey(key)
        if (numberKey != null) {
            // Mode selection update for Forklift
            val forklift = xenon.getForkliftOrNull() ?: return
            forklift.editor.selectMode(numberKey)
        }
    }
}