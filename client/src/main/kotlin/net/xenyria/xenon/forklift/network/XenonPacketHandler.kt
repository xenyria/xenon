package net.xenyria.xenon.forklift.network

import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeResponsePacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeStartPacket
import net.xenyria.xenon.protocol.serverbound.handshake.ServerboundHandshakeRequestPacket

object XenonPacketHandler {
    fun handlePacket(client: IGameClient, message: IXenonPacket): Boolean {
        if (message is ClientboundHandshakeStartPacket) {
            client.sendPacket(ServerboundHandshakeRequestPacket(client.getModVersion()))
            return true
        } else if (message is ClientboundHandshakeResponsePacket) {
            client.startSession(message.canUseEditMode)
            return true
        }
        return false
    }
}