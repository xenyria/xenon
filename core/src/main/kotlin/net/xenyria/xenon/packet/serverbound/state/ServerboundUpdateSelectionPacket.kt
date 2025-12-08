package net.xenyria.xenon.packet.serverbound.state

import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the client when the currently selected Gizmo changes.
 */
class ServerboundUpdateSelectionPacket() : IXenonPacket(XenonPacketRegistry.SERVERBOUND_UPDATE_SELECTION) {

    lateinit var selectedGizmo: UUID
        private set

    constructor(selectedGizmo: UUID) : this() {
        this.selectedGizmo = selectedGizmo
    }

    override fun deserialize(input: DataInputStream) {
        selectedGizmo = input.readUUID()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUUID(selectedGizmo)
    }

}