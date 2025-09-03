/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet

import net.xenyria.xenon.server.IXenonClient
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

const val CHANNEL_ID = "minecraft:xenon"

fun sendPacket(receiver: IXenonClient, packet: IXenonPacket) {
    val byteOut = ByteArrayOutputStream()
    val dataOut = DataOutputStream(byteOut)
    dataOut.writeByte(packet.type.id)
    packet.serialize(dataOut)
    receiver.sendPluginMessage(CHANNEL_ID, byteOut.toByteArray())
}

fun parsePacket(data: ByteArrayInputStream): IXenonPacket {
    val packetId = data.read()
    val packet = XenonPacketRegistry.createEmpty(packetId)
    packet.deserialize(DataInputStream(data))
    return packet
}