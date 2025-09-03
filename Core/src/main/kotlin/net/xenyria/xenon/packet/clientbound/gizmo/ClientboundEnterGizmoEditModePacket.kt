/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.clientbound.gizmo

import net.xenyria.xenon.core.*
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.CLIENTBOUND_ENTER_GIZMO_EDIT_MODE
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the server after a "Request Gizmo" packet has been sent.
 * If isSuccess is true, the gizmo is locked as long as the player is manipulating it.
 * During this time, nobody else can modify the selected gizmo instance.
 */
class ClientboundEnterGizmoEditModePacket() : IXenonPacket(CLIENTBOUND_ENTER_GIZMO_EDIT_MODE) {

    lateinit var gizmoId: UUID
        private set
    var data: GizmoInitData? = null
        private set
    val isSuccess: Boolean
        get() = data != null

    override fun deserialize(input: DataInputStream) {
        gizmoId = input.readUUID()
        data = input.readOptional {
            GizmoInitData(input.readVec3D(), input.readVec3D(), input.readVec3D())
        }
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUUID(gizmoId)
        output.writeOptional(data) {
            output.writeVec3D(it.position)
            output.writeVec3D(it.rotation)
            output.writeVec3D(it.scale)
        }
    }
}

data class GizmoInitData(val position: IVec3D, val rotation: IVec3D, val scale: IVec3D)
