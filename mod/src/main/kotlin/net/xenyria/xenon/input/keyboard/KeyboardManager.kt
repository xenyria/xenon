package net.xenyria.xenon.input.keyboard

import net.minecraft.client.Minecraft
import net.minecraft.client.input.KeyEvent
import net.xenyria.xenon.forklift
import net.xenyria.xenon.game
import net.xenyria.xenon.mixin.KeyboardHandlerInvoker
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

private data class HeldKey(val keyCode: Int, val scanCode: Int)

class KeyboardManager(windowId: Long, invoker: KeyboardHandlerInvoker) {

    private val keyboardInvoker: KeyboardHandlerInvoker = invoker
    val simulator = KeyboardSimulator(windowId, keyboardInvoker)

    private val _heldKeys = HashMap<Int, HeldKey>()
    private val _mutex = Any()

    fun releaseAllKeys() {
        synchronized(_mutex) {
            val keysToRelease = _heldKeys
            for (key in keysToRelease.values) {
                simulator.simulateKeyRelease(key.keyCode, key.scanCode)
            }
            _heldKeys.clear()
        }
    }

    fun onKeyPress(window: Long, action: KeyAction, keyEvent: KeyEvent, callbackInfo: CallbackInfo) {
        if (window != Minecraft.getInstance().window.handle()) return
        if (action == KeyAction.DOWN) {
            if (game.gui.chat.isChatFocused) return
            handleKeyPress(keyEvent.key)
        }

        if (forklift.editor.isMouseLocked()) {
            callbackInfo.cancel()
            return
        }

        synchronized(_mutex) {
            if (action == KeyAction.DOWN) {
                _heldKeys.remove(keyEvent.key)
            } else {
                _heldKeys[keyEvent.key] = HeldKey(keyEvent.key, keyEvent.scancode)
            }
        }
    }

    private fun handleKeyPress(key: Int) {
        val numberKey = toNumberKey(key)
        if (numberKey != null) {
            // Mode selection update for Forklift
            forklift.editor.selectMode(numberKey)
        }
    }
}