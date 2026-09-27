package net.xenyria.xenon

import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.input.keyboard.toKeyAction
import org.joml.Vector2d
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Helper object that abstracts game events and forwards them to the Xenon mod.
 */
object GameEvents {

    /**
     * Should be called whenever a key event occurs in the game.
     * This method will forward the event to the Xenon mod for processing.
     */
    fun onKeyPress(window: Long, action: Int, keyEvent: KeyEvent, callbackInfo: CallbackInfo) {
        val keyboard = xenon.getKeyboardManagerOrNull() ?: return
        val keyAction = toKeyAction(action) ?: return
        keyboard.onKeyPress(window, keyAction, keyEvent, callbackInfo)
    }

    /**
     * Should be called when the renderer is shutting down to clean up any renderer resources.
     */
    fun onRendererClose() {
        ForkliftRenderer.destroy()
    }

    /**
     * Should be called every game tick to allow Xenon to update its internal state.
     */
    fun onTick() {
        val instance = Xenon.getOrNull() ?: return
        instance.onTick()
    }

    /**
     * Should be called when the game disconnects from the server.
     */
    fun onDisconnect() {
        Xenon.getOrNull()?.endSession()
    }

    /**
     * Should be called when a mouse button is pressed or released to allow Xenon to process mouse button input.
     */
    fun onMouseButton(windowId: Long, mouseButtonInfo: MouseButtonInfo, action: Int, info: CallbackInfo) {
        if (windowId != game.window.handle()) return
        if (xenon.onMouseButton(mouseButtonInfo, action)) info.cancel()
    }

    /**
     * Should be called when the mouse is moved to allow Xenon to process mouse movement.
     */
    fun onMouseMove(windowId: Long, x: Double, y: Double, relativeX: Double, relativeY: Double, info: CallbackInfo) {
        if (windowId != game.window.handle()) return
        if (xenon.onMouseMove(Vector2d(relativeX, relativeY)))
            info.cancel()
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