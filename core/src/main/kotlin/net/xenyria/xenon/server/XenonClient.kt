package net.xenyria.xenon.server

import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.sendPacket
import org.joml.Vector3dc

interface IXenonClient {

    val cameraPosition: Vector3dc

    fun sendPluginMessage(channel: String, data: ByteArray)

    fun sendXenonMessage(packet: IXenonPacket) {
        sendPacket(this, packet)
    }

}