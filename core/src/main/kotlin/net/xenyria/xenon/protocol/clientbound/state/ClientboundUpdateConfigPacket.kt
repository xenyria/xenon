package net.xenyria.xenon.protocol.clientbound.state

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the server to set Forklift's config.
 */
class ClientboundUpdateConfigPacket : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_CONFIG) {

    lateinit var config: ForkliftConfig
        private set

    override fun deserialize(input: DataInputStream) {
        val length = input.readVarInt()
        val result = ForkliftConfig.deserializeConfig(input.readNBytes(length))
        config = result.getOrThrow()
    }

    override fun serialize(output: DataOutputStream) {
        val bytes = ForkliftConfig.serializeConfig(config)
        output.writeVarInt(bytes.size)
        output.write(bytes)
    }


}