package net.xenyria.xenon.demo.player

import net.xenyria.xenon.CHANNEL_ID
import net.xenyria.xenon.demo.XenonDemoPlugin
import net.xenyria.xenon.demo.feature.gizmo.XenonGizmos
import net.xenyria.xenon.demo.feature.overlay.XenonOverlays
import net.xenyria.xenon.demo.feature.shape.XenonShapes
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeResponsePacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeStartPacket
import net.xenyria.xenon.protocol.clientbound.state.ClientboundAcknowledgeModeSwitchPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import net.xenyria.xenon.protocol.serverbound.handshake.ServerboundHandshakeRequestPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundRequestModeSwitchPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundUpdateSelectionPacket
import net.xenyria.xenon.server.IXenonClient
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

class XenonPlayer(val player: Player) : IXenonClient {

    init {
        Bukkit.getScheduler().runTaskLater(XenonDemoPlugin.instance, { task ->
            sendXenonMessage(ClientboundHandshakeStartPacket())
        }, 10L)
    }

    fun update() {

    }

    var isActive = false // Whether the handshake process has been completed successfully
    var isEditing = false // Whether the client is currently in edit mode
    var selection: UUID? = null

    fun onMessage(message: IXenonPacket) {
        if (message is ServerboundHandshakeRequestPacket) {
            player.sendMessage("Xenon handshake completed. Client mod ver.: ${message.version}")
            sendXenonMessage(ClientboundHandshakeResponsePacket(true))
            onInit()
        } else if (message is ServerboundRequestModeSwitchPacket) {
            isEditing = message.desiredState
            player.sendMessage("Edit mode state is now $isEditing")
            sendXenonMessage(ClientboundAcknowledgeModeSwitchPacket(isEditing))
        } else if (message is ServerboundRequestGizmoPacket) {
            XenonGizmos.onRequestGizmo(this, message)
        } else if (message is ServerboundReleaseGizmoPacket) {
            XenonGizmos.onReleaseGizmo(this)
        } else if (message is ServerboundUpdateGizmoPacket) {
            XenonGizmos.updateGizmo(player, message.gizmoId, message.position, message.rotation, message.scale)
        } else if (message is ServerboundUpdateSelectionPacket) {
            selection = message.selectedGizmo
        }
    }

    private fun onInit() {
        isActive = true
        XenonShapes.spawnAll(this)
        XenonOverlays.spawnAll(this)
        XenonGizmos.spawnAll(this)
    }

    override val cameraPosition: Vector3dc
        get() {
            val eyePos = player.eyeLocation
            return Vector3d(eyePos.x, eyePos.y, eyePos.z)
        }

    override fun sendPluginMessage(channel: String, data: ByteArray) {
        player.sendPluginMessage(XenonDemoPlugin.instance, CHANNEL_ID, data)
    }

}