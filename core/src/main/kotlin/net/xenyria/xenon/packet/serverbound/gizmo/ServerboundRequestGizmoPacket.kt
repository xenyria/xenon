package net.xenyria.xenon.packet.serverbound.gizmo

import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.SERVERBOUND_REQUEST_GIZMO
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the client when the player wants to edit (translate, rotate, scale) a Gizmo.
 * Only one player can edit a Gizmo at a time.
 */
class ServerboundRequestGizmoPacket : IXenonPacket(SERVERBOUND_REQUEST_GIZMO) {

    lateinit var gizmoId: UUID
        private set

    override fun deserialize(input: DataInputStream) {
        gizmoId = input.readUUID()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUUID(gizmoId)
    }
}