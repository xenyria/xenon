package net.xenyria.xenon.packet

import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IXenonPacket(val type: XenonPacketType) {

    abstract fun deserialize(input: DataInputStream)

    abstract fun serialize(output: DataOutputStream)

}

abstract class IEmptyXenonPacket(type: XenonPacketType) : IXenonPacket(type) {
    override fun deserialize(input: DataInputStream) {
    }

    override fun serialize(output: DataOutputStream) {
    }
}