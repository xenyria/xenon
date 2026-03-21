package net.xenyria.xenon.packet.clientbound.gizmo

import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.readVec3D
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.core.writeVec3D
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.CLIENTBOUND_UPDATE_GIZMO
import org.joml.Vector3dc
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the server to synchronize the list of available Gizmos.
 */
class ClientboundUpdateGizmoPacket() : IXenonPacket(CLIENTBOUND_UPDATE_GIZMO) {

    constructor(uuid: UUID, position: Vector3dc, rotation: Vector3dc, scale: Vector3dc) : this() {
        this.uuid = uuid
        this.position = position
        this.rotation = rotation
        this.scale = scale
    }

    lateinit var uuid: UUID
        private set
    lateinit var position: Vector3dc
        private set
    lateinit var rotation: Vector3dc
        private set
    lateinit var scale: Vector3dc
        private set

    override fun deserialize(input: DataInputStream) {
        uuid = input.readUUID()
        position = input.readVec3D()
        rotation = input.readVec3D()
        scale = input.readVec3D()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUUID(uuid)
        output.writeVec3D(position)
        output.writeVec3D(rotation)
        output.writeVec3D(scale)
    }

}