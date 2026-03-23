package net.xenyria.xenon.protocol.clientbound.camera

import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.core.readOptional
import net.xenyria.xenon.core.writeOptional
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry.CLIENTBOUND_UPDATE_CAMERA_LOCK
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Packet sent by the server to lock (+ update) the current camera mode.
 */
class ClientboundUpdateCameraLockPacket() : IXenonPacket(CLIENTBOUND_UPDATE_CAMERA_LOCK) {

    var isLocked = false

    /**
     * The mode that should be set for the player's camera mode setting.
     * If set to any value, the player's camera is updated at the same time the lock state is being updated.
     */
    var newMode: CameraPerspective? = null

    constructor(isLocked: Boolean, newMode: CameraPerspective?) : this() {
        this.isLocked = isLocked
        this.newMode = newMode
    }

    override fun deserialize(input: DataInputStream) {
        isLocked = input.readBoolean()
        newMode = input.readOptional { CameraPerspective.entries[it.readByte().toInt()] }
    }

    override fun serialize(output: DataOutputStream) {
        output.writeBoolean(isLocked)
        output.writeOptional(newMode) { output.writeByte(it.ordinal) }
    }

}