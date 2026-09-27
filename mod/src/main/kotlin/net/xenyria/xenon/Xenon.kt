package net.xenyria.xenon

import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonInfo
import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.config.Settings
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.config.XenonConfig
import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.forklift.Forklift
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.forklift.render.XenonRenderPipelines
import net.xenyria.xenon.forklift.render.overlay.ForkliftOverlayRenderer
import net.xenyria.xenon.input.keyboard.KeyboardManager
import net.xenyria.xenon.input.mouse.fromSDL
import net.xenyria.xenon.network.XenonPacketListener
import net.xenyria.xenon.protocol.IXenonPacket
import org.joml.Vector2d
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("Xenon")

/**
 * Class that represents an instance of Xenon.
 */
class Xenon(val version: String) {

    private var session: Session? = null
    private var _keyboard: KeyboardManager? = null
    private var pendingPacket: IXenonPacket? = null

    val client = GameClient(this)
    val config: XenonConfig get() = XenonClientConfig.config
    val forklift: Forklift get() = requireNotNull(getForkliftOrNull()) { "Not connected to any supported server" }
    val keyboard: KeyboardManager get() = requireNotNull(_keyboard) { "Keyboard functionality is not ready yet" }
    fun getKeyboardManagerOrNull(): KeyboardManager? = _keyboard

    fun getForkliftOrNull(): Forklift? {
        return session?.forklift
    }

    init {
        LOGGER.info("Starting up Xenon...")
    }

    fun initialize(game: Minecraft) {
        require(Minecraft.getInstance().window.handle() != 0L) { "Window is not initialized" }
        _keyboard = KeyboardManager(game.window.handle())
        XenonRenderPipelines.initialize()
        ForkliftRenderer.initialize()
        ForkliftOverlayRenderer.initialize(this)
        LOGGER.info("Xenon (v${version}) has been initialized.")
    }

    fun onTick() {
        getForkliftOrNull()?.onTick()
        sendPendingPacket()
    }

    fun debouncePacket(packet: IXenonPacket) {
        pendingPacket = packet
    }

    private fun sendPendingPacket() {
        val packet = pendingPacket ?: return
        client.sendPacket(packet)
        pendingPacket = null
    }

    fun endSession() {
        getForkliftOrNull()?.reset()
        session?.destroy()
        this.session = null
    }

    fun onMouseButton(mouseButtonInfo: MouseButtonInfo, action: Int): Boolean {
        if (game.gui.screen() != null) return false
        val forklift = xenon.getForkliftOrNull() ?: return false

        val event = fromSDL(mouseButtonInfo.button, action)
        if (event.isRightMouseButton && event.isReleased) forklift.editorClient.leaveDragMode()

        if (forklift.editorClient.onMouseButton(event) && forklift.editorClient.isActive) {
            keyboard.releaseAllKeys()
            return true
        }
        return false
    }

    fun shouldShiftHud(): Boolean {
        return getForkliftOrNull()?.editorClient?.isActive ?: false
    }

    fun toggleEditMode(): Boolean {
        if (!client.xenonConfig.developer.enableGizmos) return false
        val forklift = getForkliftOrNull() ?: return false
        return forklift.editorClient.toggleEditMode()
    }

    /**
     * Called when the user moves their mouse.
     * @return When true, the input event is discarded - meaning it should not be passed to the game.
     */
    fun onMouseMove(mouseDelta: Vector2d): Boolean {
        val forklift = getForkliftOrNull() ?: return false
        if (forklift.editorClient.isMouseLocked()) {
            forklift.editorClient.onMouseMove(mouseDelta)
            return true
        }
        return false
    }

    fun getSessionOrNull(): Session? {
        return session
    }

    fun startSession(editModeAvailable: Boolean) {
        session = Session(client, editModeAvailable)
    }

    fun updateActivityAppId(appId: Long) {
        session?.updateActivityAppId(appId)
    }

    fun updateActivity(activityData: ActivityData) {
        session?.updateActivity(activityData)
    }

    fun requestCameraPerspective(perspective: CameraPerspective) {
        session?.requestCameraPerspective(perspective)
    }

    fun updateCameraLock(locked: Boolean, newMode: CameraPerspective?) {
        session?.updateCameraLock(locked, newMode)
    }

    fun isCameraModeLocked(): Boolean {
        return session?.isCameraModeLocked() ?: return false
    }

    companion object {
        private var _xenon: Xenon? = null
        val instance: Xenon get() = requireNotNull(_xenon) { "Xenon is not initialized" }

        fun getOrNull(): Xenon? {
            return _xenon
        }

        fun create(version: String) {
            XenonPacketListener.initialize()
            Settings.create()
            Keybinds.register()
            _xenon = Xenon(version)
        }
    }
}

val game: Minecraft get() = Minecraft.getInstance()
val xenon: Xenon get() = Xenon.instance