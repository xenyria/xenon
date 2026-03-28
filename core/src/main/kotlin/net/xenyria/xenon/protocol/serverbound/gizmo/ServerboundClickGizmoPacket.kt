package net.xenyria.xenon.protocol.serverbound.gizmo

import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry.SERVERBOUND_CLICK_GIZMO
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class ServerboundClickGizmoPacket() : IXenonPacket(SERVERBOUND_CLICK_GIZMO) {

    constructor(gizmoId: UUID) : this() {
        this.gizmoId = gizmoId
    }

    lateinit var gizmoId: UUID
        private set

    override fun deserialize(input: DataInputStream) {
        gizmoId = input.readUUID()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUUID(gizmoId)
    }
}