package net.xenyria.xenon

import net.minecraft.world.phys.Vec3
import net.xenyria.xenon.core.calculateDirection
import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.editor.Editor
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.mixin.MouseInvoker
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.util.toComponent
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

class GameClient(xenon: Xenon) : IGameClient {
    override fun getCamera(): GameCamera {
        val cam = game.gameRenderer.mainCamera
        return GameCamera(
            Vector3d(cam.position().x, cam.position().y, cam.position().z),
            calculateDirection(cam.yRot(), cam.xRot())
        )
    }

    override fun getMousePosition(): Vector2d {
        return Vector2d(
            game.mouseHandler.xpos(),
            game.mouseHandler.ypos()
        )
    }

    override fun sendPacket(packet: IXenonPacket) {
    }

    override fun getScreenPosition(worldPosition: Vector3dc): Vector2d {
        val projected = game.gameRenderer.projectPointToScreen(Vec3(worldPosition.x(), worldPosition.y(), worldPosition.z()))
        // TODO: This is technically incorrect
        return Vector2d(projected.x, (game.window.height - 1) - projected.y)
    }

    override fun sendMessage(message: Message) {
        game.chatListener.handleSystemMessage(message.toComponent(), false)
    }

    override fun hasShiftDown(): Boolean {
        return game.hasShiftDown()
    }

    override fun hasControlDown(): Boolean {
        return game.hasControlDown()
    }

    override fun hasAltDown(): Boolean {
        return game.hasAltDown()
    }

    override fun renderGizmos(renderList: List<RenderableGizmo>) {
        ForkliftRenderer.updateGizmos(renderList)
    }

    override fun updateInternalMousePosition(x: Double, y: Double) {
        val invoker = (game.mouseHandler as MouseInvoker)
        invoker.setX(x)
        invoker.setY(y)
    }

    override fun getPlayerId(): UUID? {
        return game.player?.uuid
    }

    override val config: ForkliftConfig = ForkliftConfig()
    override val editor: Editor get() = xenon.forklift.editor
}