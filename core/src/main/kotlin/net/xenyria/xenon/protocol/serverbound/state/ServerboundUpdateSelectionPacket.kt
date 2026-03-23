package net.xenyria.xenon.protocol.serverbound.state

import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the client when the currently selected Gizmo changes.
 */
class ServerboundUpdateSelectionPacket() : IXenonPacket(XenonPacketRegistry.SERVERBOUND_UPDATE_SELECTION) {

    var selectedGizmo: UUID? = null

    constructor(selectedGizmo: UUID?) : this() {
        this.selectedGizmo = selectedGizmo
    }

    override fun deserialize(input: DataInputStream) {
        selectedGizmo = if (input.readBoolean())
            input.readUUID()
        else
            null
    }

    override fun serialize(output: DataOutputStream) {
        val gizmo = selectedGizmo
        output.writeBoolean(gizmo != null)
        if (gizmo != null) output.writeUUID(gizmo)
    }

}