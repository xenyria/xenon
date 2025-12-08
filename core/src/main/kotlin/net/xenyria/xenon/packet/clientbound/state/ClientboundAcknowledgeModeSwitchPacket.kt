package net.xenyria.xenon.packet.clientbound.state

import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server to put the player in or out of edit mode.
 */
class ClientboundAcknowledgeModeSwitchPacket() : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_ACKNOWLEDGE_MODE_SWITCH) {

    var editModeEnabled: Boolean = false
        private set

    constructor(editModeEnabled: Boolean) : this() {
        this.editModeEnabled = editModeEnabled
    }

    override fun deserialize(input: DataInputStream) {
        editModeEnabled = input.readBoolean()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeBoolean(editModeEnabled)
    }
}