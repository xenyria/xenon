package net.xenyria.xenon.server

import net.xenyria.xenon.core.Vec3D
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.sendPacket

interface IXenonClient {

    val cameraPosition: Vec3D

    fun sendPluginMessage(channel: String, data: ByteArray)

    fun sendXenonMessage(packet: IXenonPacket) {
        sendPacket(this, packet)
    }

}