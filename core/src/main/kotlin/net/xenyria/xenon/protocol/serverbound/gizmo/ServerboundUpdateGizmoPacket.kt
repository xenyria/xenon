package net.xenyria.xenon.protocol.serverbound.gizmo

import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.readVec3D
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.core.writeVec3D
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry.SERVERBOUND_UPDATE_GIZMO
import org.joml.Vector3dc
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the client when the player wants to edit (translate, rotate, scale) a Gizmo.
 * Only one player can edit a Gizmo at a time.
 */
class ServerboundUpdateGizmoPacket() : IXenonPacket(SERVERBOUND_UPDATE_GIZMO) {

    constructor(gizmoId: UUID, position: Vector3dc, rotation: Vector3dc, scale: Vector3dc) : this() {
        this.gizmoId = gizmoId
        this.position = position
        this.rotation = rotation
        this.scale = scale
    }

    lateinit var gizmoId: UUID
        private set
    lateinit var position: Vector3dc
        private set
    lateinit var rotation: Vector3dc
        private set
    lateinit var scale: Vector3dc
        private set

    override fun deserialize(input: DataInputStream) {
        gizmoId = input.readUUID()
        position = input.readVec3D()
        rotation = input.readVec3D()
        scale = input.readVec3D()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUUID(gizmoId)
        output.writeVec3D(position)
        output.writeVec3D(rotation)
        output.writeVec3D(scale)
    }
}