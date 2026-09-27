package net.xenyria.xenon.protocol.clientbound.misc

import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server to update the Discord activity of the player.
 */
class ClientboundUpdateActivityPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_ACTIVITY) {

    constructor(activityData: ActivityData) : this() {
        this.activityData = activityData
    }

    lateinit var activityData: ActivityData

    override fun deserialize(input: DataInputStream) {
        activityData = ActivityData.read(input)
    }

    override fun serialize(output: DataOutputStream) {
        ActivityData.write(activityData, output)
    }
}