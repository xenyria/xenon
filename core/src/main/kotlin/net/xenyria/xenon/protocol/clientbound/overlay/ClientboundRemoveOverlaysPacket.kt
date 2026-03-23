package net.xenyria.xenon.protocol.clientbound.overlay

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server when a list of overlays should be removed.
 */
class ClientboundRemoveOverlaysPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_REMOVE_OVERLAYS) {

    constructor(overlayIds: List<String>) : this() {
        this.overlays = overlayIds
    }

    var overlays: List<String> = emptyList()
        private set

    override fun deserialize(input: DataInputStream) {
        val overlayIds = ArrayList<String>()
        repeat(input.readVarInt()) {
            overlayIds.add(input.readUTF())
        }
        this.overlays = overlayIds
    }

    override fun serialize(output: DataOutputStream) {
        output.writeVarInt(overlays.size)
        for (overlay in overlays) {
            output.writeUTF(overlay)
        }
    }

}