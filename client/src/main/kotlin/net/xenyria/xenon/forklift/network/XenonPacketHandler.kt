package net.xenyria.xenon.forklift.network

import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.clientbound.camera.ClientboundSetCameraPerspectivePacket
import net.xenyria.xenon.protocol.clientbound.camera.ClientboundUpdateCameraLockPacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeResponsePacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeStartPacket
import net.xenyria.xenon.protocol.clientbound.misc.ClientboundUpdateActivityAppPacket
import net.xenyria.xenon.protocol.clientbound.misc.ClientboundUpdateActivityPacket
import net.xenyria.xenon.protocol.serverbound.handshake.ServerboundHandshakeRequestPacket

object XenonPacketHandler {
    fun handlePacket(client: IGameClient, message: IXenonPacket): Boolean {
        when (message) {
            is ClientboundHandshakeStartPacket -> {
                client.sendPacket(ServerboundHandshakeRequestPacket(client.getModVersion()))
                return true
            }

            is ClientboundUpdateActivityPacket -> {
                client.updateActivity(message.activityData)
                return true
            }

            is ClientboundUpdateActivityAppPacket -> {
                client.updateActivityAppId(message.appId)
                return true
            }

            is ClientboundHandshakeResponsePacket -> {
                client.startSession(message.canUseEditMode)
                return true
            }

            is ClientboundSetCameraPerspectivePacket -> {
                client.requestCameraPerspective(message.perspective)
                return true
            }

            is ClientboundUpdateCameraLockPacket -> {
                client.updateCameraLock(message.isLocked, message.newMode)
                return true
            }

            else -> return false
        }
    }
}