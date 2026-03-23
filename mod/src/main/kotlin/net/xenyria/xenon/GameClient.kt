package net.xenyria.xenon

import net.minecraft.client.CameraType
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.world.phys.Vec3
import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.config.XenonConfig
import net.xenyria.xenon.core.calculateDirection
import net.xenyria.xenon.discord.ActivityData
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

    override fun updateActivity(activityData: ActivityData) {
        xenon.updateActivity(activityData)
    }

    override fun updateActivityAppId(appId: Long) {
        xenon.updateActivityAppId(appId)
    }

    override fun updateCameraLock(isLocked: Boolean, newMode: CameraPerspective?) {
        xenon.updateCameraLock(isLocked, newMode)
    }

    override fun requestCameraPerspective(perspective: CameraPerspective) {
        xenon.requestCameraPerspective(perspective)
    }

    @Synchronized
    override fun updateInternalMousePosition(x: Double, y: Double) {
        val invoker = (game.mouseHandler as MouseInvoker)
        invoker.setX(x)
        invoker.setY(y)
    }

    private fun mapPerspective(cameraType: CameraType): CameraPerspective {
        return when (cameraType) {
            CameraType.FIRST_PERSON -> CameraPerspective.FIRST_PERSON
            CameraType.THIRD_PERSON_FRONT -> CameraPerspective.THIRD_PERSON_FRONT
            CameraType.THIRD_PERSON_BACK -> CameraPerspective.THIRD_PERSON_BACK
        }
    }

    private fun mapCameraType(perspective: CameraPerspective): CameraType {
        return when (perspective) {
            CameraPerspective.FIRST_PERSON -> CameraType.FIRST_PERSON
            CameraPerspective.THIRD_PERSON_FRONT -> CameraType.THIRD_PERSON_FRONT
            CameraPerspective.THIRD_PERSON_BACK -> CameraType.THIRD_PERSON_BACK
        }
    }

    override fun setCameraPerspective(perspective: CameraPerspective) {
        game.options.cameraType = mapCameraType(perspective)
    }

    override fun getCameraPerspective(): CameraPerspective {
        return mapPerspective(game.options.cameraType)
    }

    @Synchronized
    override fun getPlayerId(): UUID? {
        return game.player?.uuid
    }

    override val forkliftConfig: ForkliftConfig = ForkliftConfig()
    override val xenonConfig: XenonConfig get() = XenonClientConfig.config
    override val editor: Editor get() = xenon.forklift.editor
}