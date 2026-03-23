package net.xenyria.xenon

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.world.phys.Vec3
import net.xenyria.xenon.core.calculateDirection
import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.editor.Editor
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.overlay.TextOverlayData
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.forklift.render.overlay.ForkliftOverlayRenderer
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.mixin.MouseInvoker
import net.xenyria.xenon.network.XenonPayload
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.convertPacketToBytes
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.util.toComponent
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

class GameClient(xenon: Xenon) : IGameClient {

    @Synchronized
    override fun getCamera(): GameCamera {
        val cam = game.gameRenderer.mainCamera
        return GameCamera(
            Vector3d(cam.position().x, cam.position().y, cam.position().z),
            calculateDirection(cam.yRot(), cam.xRot())
        )
    }

    @Synchronized
    override fun getMousePosition(): Vector2d {
        return Vector2d(
            game.mouseHandler.xpos(),
            game.mouseHandler.ypos()
        )
    }

    @Synchronized
    override fun sendPacket(packet: IXenonPacket) {
        // Create a plugin message packet
        val payload = XenonPayload(convertPacketToBytes(packet))
        val minecraftPacket = ServerboundCustomPayloadPacket(payload)
        game.connection?.send(minecraftPacket)
    }

    @Synchronized
    override fun getScreenPosition(worldPosition: Vector3dc): Vector2d {
        val projected = game.gameRenderer.projectPointToScreen(Vec3(worldPosition.x(), worldPosition.y(), worldPosition.z()))
        // TODO: This is technically incorrect
        return Vector2d(projected.x, (game.window.height - 1) - projected.y)
    }

    @Synchronized
    override fun sendMessage(message: Message) {
        game.chatListener.handleSystemMessage(message.toComponent(), false)
    }

    @Synchronized
    override fun hasShiftDown(): Boolean {
        return game.hasShiftDown()
    }

    @Synchronized
    override fun hasControlDown(): Boolean {
        return game.hasControlDown()
    }

    @Synchronized
    override fun hasAltDown(): Boolean {
        return game.hasAltDown()
    }

    @Synchronized
    override fun renderGizmos(renderList: List<RenderableGizmo>) {
        ForkliftRenderer.updateGizmos(renderList)
    }

    @Synchronized
    override fun renderShapes(shapes: List<IEditorShape<*>>) {
        ForkliftRenderer.updateShapes(shapes)
    }

    @Synchronized
    override fun renderOverlays(overlays: List<TextOverlayData>) {
        ForkliftOverlayRenderer.updateOverlays(overlays)
    }

    @Synchronized
    override fun startSession(canUseEditMode: Boolean) {
        xenon.startSession(canUseEditMode)
    }

    @Synchronized
    override fun getModVersion(): String {
        return xenon.version
    }

    @Synchronized
    override fun debounceGizmoPacket(packet: ServerboundUpdateGizmoPacket) {
        xenon.debouncePacket(packet)
    }

    override fun isDragging(): Boolean {
        return xenon.getForkliftOrNull()?.editor?.dragHandler?.isActive() ?: false
    }

    @Synchronized
    override fun updateInternalMousePosition(x: Double, y: Double) {
        val invoker = (game.mouseHandler as MouseInvoker)
        invoker.setX(x)
        invoker.setY(y)
    }

    @Synchronized
    override fun getPlayerId(): UUID? {
        return game.player?.uuid
    }

    override val config: ForkliftConfig = ForkliftConfig()
    override val editor: Editor get() = xenon.forklift.editor
}