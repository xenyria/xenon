package net.xenyria.xenon.server

import net.xenyria.xenon.protocol.sendPacket
import org.joml.Vector3dc

interface IXenonClient {

    val cameraPosition: Vector3dc

    fun sendPluginMessage(channel: String, data: ByteArray)

    fun sendXenonMessage(packet: net.xenyria.xenon.protocol.IXenonPacket) {
        sendPacket(this, packet)
    }

}