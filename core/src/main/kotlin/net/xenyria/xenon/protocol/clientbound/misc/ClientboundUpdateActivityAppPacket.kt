package net.xenyria.xenon.protocol.clientbound.misc

import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

class ClientboundUpdateActivityAppPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_ACTIVITY_APP) {

    constructor(appId: Long) : this() {
        this.appId = appId
    }

    var appId: Long = 0L

    override fun deserialize(input: DataInputStream) {
        appId = input.readLong()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeLong(appId)
    }
}