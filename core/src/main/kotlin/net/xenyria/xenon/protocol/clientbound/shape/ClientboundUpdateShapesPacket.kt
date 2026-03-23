package net.xenyria.xenon.protocol.clientbound.shape

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.ShapeType
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server to add (or updates) shapes
 */
class ClientboundUpdateShapesPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_SHAPES) {

    constructor(list: List<IEditorShape<*>>) : this() {
        this.shapes = list
    }

    var shapes: List<IEditorShape<*>> = emptyList()
        private set

    override fun deserialize(input: DataInputStream) {
        val size = input.readVarInt()
        val shapeList = ArrayList<IEditorShape<*>>()
        repeat(size) {
            val type = ShapeType.entries[input.readByte().toInt()]
            val shape = type.parseShape(input)
            shapeList.add(shape)
        }
        this.shapes = shapeList
    }

    override fun serialize(output: DataOutputStream) {
        output.writeVarInt(shapes.size)
        for (shape in shapes) {
            output.writeByte(shape.type.ordinal)
            shape.serialize(output)
        }
    }

}