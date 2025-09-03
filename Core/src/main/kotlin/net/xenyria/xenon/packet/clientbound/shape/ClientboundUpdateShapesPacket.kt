/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.clientbound.shape

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.ShapeType
import java.io.DataInputStream
import java.io.DataOutputStream

class ClientboundUpdateShapesPacket : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_SHAPES) {

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