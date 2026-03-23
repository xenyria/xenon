package net.xenyria.xenon.protocol.clientbound.handshake

import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the server in response to [ClientboundHandshakeStartPacket] indicating which features the client can use.
 */
class ClientboundHandshakeResponsePacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_HANDSHAKE_RESPONSE) {

    constructor(canUseEditMode: Boolean) : this() {
        this.canUseEditMode = canUseEditMode
    }

    var canUseEditMode: Boolean = false // Level editor mode feature flag
        private set

    override fun deserialize(input: DataInputStream) {
        canUseEditMode = input.readBoolean()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeBoolean(canUseEditMode)
    }

}