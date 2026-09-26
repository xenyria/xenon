package net.xenyria.xenon.input.keyboard

import net.minecraft.client.Minecraft
import net.minecraft.client.input.KeyEvent
import net.xenyria.xenon.game
import net.xenyria.xenon.xenon
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

private data class HeldKey(val keyCode: Int, val scanCode: Int)

class KeyboardManager(windowId: Long) {

    private val heldKeys = HashMap<Int, HeldKey>()
    val simulator = KeyboardSimulator(windowId)

    @Synchronized
    fun releaseAllKeys() {
        val keysToRelease = heldKeys
        for (key in keysToRelease.values) {
            simulator.simulateKeyRelease(key.keyCode, key.scanCode)
        }
        heldKeys.clear()
    }

    fun onKeyPress(window: Long, action: KeyAction, keyEvent: KeyEvent, callbackInfo: CallbackInfo) {
        if (window != Minecraft.getInstance().window.handle()) return
        if (action == KeyAction.DOWN) {
            if (game.gui.hud.chat.isChatFocused) return
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
                heldKeys[keyEvent.key] = HeldKey(keyEvent.key, keyEvent.keycode)
            } else if (action == KeyAction.UP) {
                heldKeys.remove(keyEvent.key)
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