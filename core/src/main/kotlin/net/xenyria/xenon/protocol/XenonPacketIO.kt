package net.xenyria.xenon.protocol

import net.xenyria.xenon.CHANNEL_ID
import net.xenyria.xenon.server.IXenonClient
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

fun convertPacketToBytes(packet: IXenonPacket): ByteArray {
    val byteOut = ByteArrayOutputStream()
    val dataOut = DataOutputStream(byteOut)
    dataOut.writeByte(packet.type.id)
    packet.serialize(dataOut)
    return byteOut.toByteArray()
}

fun sendPacket(receiver: IXenonClient, packet: IXenonPacket) {
    val bytes = convertPacketToBytes(packet)
    receiver.sendPluginMessage(CHANNEL_ID, bytes)
}

fun parsePacket(data: ByteArrayInputStream): IXenonPacket {
    val packetId = data.read()
    val packet = XenonPacketRegistry.createEmpty(packetId)
    packet.deserialize(DataInputStream(data))
    return packet
}