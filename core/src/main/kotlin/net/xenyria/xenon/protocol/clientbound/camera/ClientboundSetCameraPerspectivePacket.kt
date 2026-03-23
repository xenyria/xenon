package net.xenyria.xenon.protocol.clientbound.camera

import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry.CLIENTBOUND_UPDATE_CAMERA_PERSPECTIVE
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server to update the current camera mode.
 */
class ClientboundSetCameraPerspectivePacket() : IXenonPacket(CLIENTBOUND_UPDATE_CAMERA_PERSPECTIVE) {

    constructor(perspective: CameraPerspective) : this() {
        this.perspective = perspective
    }

    lateinit var perspective: CameraPerspective

    override fun serialize(output: DataOutputStream) {
        output.writeByte(perspective.ordinal)
    }

    override fun deserialize(input: DataInputStream) {
        perspective = CameraPerspective.entries[input.readByte().toInt()]
    }

}