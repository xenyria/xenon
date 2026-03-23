package net.xenyria.xenon.protocol

import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IXenonPacket(val type: net.xenyria.xenon.protocol.XenonPacketType) {

    abstract fun deserialize(input: DataInputStream)

    abstract fun serialize(output: DataOutputStream)

}

abstract class IEmptyXenonPacket(type: net.xenyria.xenon.protocol.XenonPacketType) : net.xenyria.xenon.protocol.IXenonPacket(type) {
    override fun deserialize(input: DataInputStream) {
    }

    override fun serialize(output: DataOutputStream) {
    }
}