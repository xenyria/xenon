package net.xenyria.xenon.protocol.clientbound.overlay

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.forklift.overlay.TextOverlayData
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry.CLIENTBOUND_UPDATE_OVERLAYS
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Adds (or updates) a list of text overlays.
 */
class ClientboundUpdateOverlaysPacket() : IXenonPacket(CLIENTBOUND_UPDATE_OVERLAYS) {

    constructor(overlays: List<TextOverlayData>) : this() {
        this.overlays = overlays
    }

    var overlays: List<TextOverlayData> = emptyList()
        private set

    override fun deserialize(input: DataInputStream) {
        val overlayList = ArrayList<TextOverlayData>()
        repeat(input.readVarInt()) {
            overlayList.add(TextOverlayData.fromStream(input))
        }
        overlays = overlayList
    }

    override fun serialize(output: DataOutputStream) {
        output.writeVarInt(overlays.size)
        for (overlay in overlays) {
            overlay.writeToStream(output)
        }
    }

}