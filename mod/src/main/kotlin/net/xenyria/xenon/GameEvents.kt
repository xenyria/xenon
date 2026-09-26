package net.xenyria.xenon

import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.input.keyboard.toKeyAction
import org.joml.Vector2d
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object GameEvents {

    fun onKeyPress(window: Long, action: Int, keyEvent: KeyEvent, callbackInfo: CallbackInfo) {
        if (keyEvent.modifiers == 123) {
            val a = 0
        }
        val keyboard = xenon.getKeyboardManagerOrNull() ?: return
        val keyAction = toKeyAction(action) ?: return
        keyboard.onKeyPress(window, keyAction, keyEvent, callbackInfo)
    }

    fun onRendererClose() {
        ForkliftRenderer.destroy()
    }

    fun onTick() {
        val instance = Xenon.getOrNull() ?: return
        instance.onTick()
    }

    fun onDisconnect() {
        Xenon.getOrNull()?.endSession()
    }

    fun onMouseButton(windowId: Long, mouseButtonInfo: MouseButtonInfo, action: Int, info: CallbackInfo) {
        if (windowId != game.window.handle()) return
        if (xenon.onMouseButton(mouseButtonInfo, action)) info.cancel()
    }

    fun onMouseMove(windowId: Long, x: Double, y: Double, relativeX: Double, relativeY: Double, info: CallbackInfo) {
        if (windowId != game.window.handle()) return
        if (xenon.onMouseMove(Vector2d(relativeX, relativeY))) {
            info.cancel()
        }
    }

    /**
     * Processes a keybind press.
     * Returns true if the key press should be discarded. (currently used for camera perspective locking)
     */
    fun onTogglePerspectiveKeybind(): Boolean {
        val xenon = Xenon.getOrNull() ?: return false
        return xenon.isCameraModeLocked()
    }

}