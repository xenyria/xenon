package net.xenyria.xenon

import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonInfo
import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.config.Settings
import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.forklift.Forklift
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.forklift.render.XenonRenderPipelines
import net.xenyria.xenon.forklift.render.overlay.ForkliftOverlayRenderer
import net.xenyria.xenon.input.keyboard.KeyboardManager
import net.xenyria.xenon.input.mouse.fromLWJGL
import net.xenyria.xenon.mixin.KeyboardHandlerInvoker
import net.xenyria.xenon.network.XenonPacketListener
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import org.joml.Vector2d
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("Xenon")

class Xenon(val version: String) {

    val client = GameClient(this)
    val forklift: Forklift get() = requireNotNull(getForkliftOrNull()) { "Not connected to any supported server" }

    fun getForkliftOrNull(): Forklift? {
        return _session?.forklift
    }

    private var _session: Session? = null

    private var _keyboard: KeyboardManager? = null
    val keyboard: KeyboardManager get() = requireNotNull(_keyboard) { "Keyboard functionality is not ready yet" }
    fun getKeyboardManagerOrNull(): KeyboardManager? = _keyboard

    init {
        LOGGER.info("Starting up Xenon...")
    }

    fun initialize(game: Minecraft) {
        require(Minecraft.getInstance().window.handle() != 0L) { "Window is not initialized" }
        _keyboard = KeyboardManager(
            game.window.handle(),
            game.keyboardHandler as KeyboardHandlerInvoker
        )
        XenonRenderPipelines.initialize()
        ForkliftRenderer.initialize()
        ForkliftOverlayRenderer.initialize(this)
        LOGGER.info("Xenon (v${version}) has been initialized.")
    }

    fun onTick() {
        getForkliftOrNull()?.onTick()
        val packet = _pendingPacket
        if (packet != null) {
            client.sendPacket(packet)
            _pendingPacket = null
        }
    }

    fun endSession() {
        getForkliftOrNull()?.reset()
        _session?.destroy()
        this._session = null
    }

    fun onMouseButton(mouseButtonInfo: MouseButtonInfo, action: Int): Boolean {
        if (game.screen != null) return false
        val event = fromLWJGL(mouseButtonInfo.button, action, mouseButtonInfo.modifiers)
        if (event.isRightMouseButton && event.isReleased) forklift.editor.leaveDragMode()

        val forklift = getForkliftOrNull() ?: return false

        if (forklift.editor.onMouseButton(event) && forklift.editor.isActive) {
            keyboard.releaseAllKeys()
            return true
        }
        return false
    }

    fun shouldShiftHud(): Boolean {
        return getForkliftOrNull()?.editor?.isActive ?: return false
    }

    fun toggleEditMode(): Boolean {
        if (!client.xenonConfig.developer.enableGizmos) return false
        val forklift = getForkliftOrNull() ?: return false
        return forklift.editor.toggleEditMode()
    }

    /**
     * Called when the user moves their mouse.
     * @return When true, the input event is discarded - meaning it's not passed to the game.
     */
    fun onMouseMove(mousePosition: Vector2d): Boolean {
        val forklift = getForkliftOrNull() ?: return false
        if (forklift.editor.isMouseLocked()) {
            forklift.editor.onMouseMove(mousePosition)
            return true
        }
        return false
    }

    fun getSessionOrNull(): Session? {
        return _session
    }

    fun startSession(editModeAvailable: Boolean) {
        _session = Session(client, editModeAvailable)
    }

    private var _pendingPacket: IXenonPacket? = null
    fun debouncePacket(packet: ServerboundUpdateGizmoPacket) {
        _pendingPacket = packet
    }

    fun updateActivityAppId(appId: Long) {
        _session?.updateActivityAppId(appId)
    }

    fun updateActivity(activityData: ActivityData) {
        _session?.updateActivity(activityData)
    }

    fun requestCameraPerspective(perspective: CameraPerspective) {
        _session?.requestCameraPerspective(perspective)
    }

    fun updateCameraLock(locked: Boolean, newMode: CameraPerspective?) {
        _session?.updateCameraLock(locked, newMode)
    }

    fun isCameraModeLocked(): Boolean {
        return _session?.isCameraModeLocked() ?: return false
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