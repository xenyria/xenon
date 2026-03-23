package net.xenyria.xenon.protocol.serverbound.handshake

import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the server to initiate Xenon's handshake sequence.
 */
class ServerboundHandshakeRequestPacket() : IXenonPacket(XenonPacketRegistry.SERVERBOUND_HANDSHAKE_REQUEST) {

    constructor(version: String) : this() {
        this.version = version
    }

    lateinit var version: String

    override fun deserialize(input: DataInputStream) {
        version = input.readUTF()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUTF(version)
    }

}