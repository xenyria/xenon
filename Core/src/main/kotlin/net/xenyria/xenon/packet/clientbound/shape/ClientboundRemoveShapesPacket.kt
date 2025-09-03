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
import java.io.DataInputStream
import java.io.DataOutputStream

class ClientboundRemoveShapesPacket : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_REMOVE_SHAPES) {

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