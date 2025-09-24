package net.xenyria.xenon.packet.clientbound.overlay

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.forklift.overlay.TextOverlayData
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

class ClientboundUpdateOverlaysPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_OVERLAYS) {

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