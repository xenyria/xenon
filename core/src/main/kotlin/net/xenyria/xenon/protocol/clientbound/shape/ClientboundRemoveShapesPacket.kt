package net.xenyria.xenon.protocol.clientbound.shape

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server to remove a list of shapes
 */
@Suppress("unused")
class ClientboundRemoveShapesPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_REMOVE_SHAPES) {

    constructor(shapes: List<String>) : this() {
        shapeIds = shapes
    }

    var shapeIds: List<String> = emptyList()
        private set

    override fun deserialize(input: DataInputStream) {
        val amount = input.readVarInt()
        val ids = ArrayList<String>()
        repeat(amount) {
            ids.add(input.readUTF())
        }
    }

    override fun serialize(output: DataOutputStream) {
        output.writeVarInt(shapeIds.size)
        for (id in shapeIds) {
            output.writeUTF(id)
        }
    }

}