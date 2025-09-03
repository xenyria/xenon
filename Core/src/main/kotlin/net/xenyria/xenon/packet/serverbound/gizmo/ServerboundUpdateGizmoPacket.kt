/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.serverbound.gizmo

import net.xenyria.xenon.core.*
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.SERVERBOUND_UPDATE_GIZMO
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the client when the player wants to edit (translate, rotate, scale) a Gizmo.
 * Only one player can edit a Gizmo at a time.
 */
class ServerboundUpdateGizmoPacket : IXenonPacket(SERVERBOUND_UPDATE_GIZMO) {

    lateinit var gizmoId: UUID
        private set
    lateinit var position: IVec3D
        private set
    lateinit var rotation: IVec3D
        private set
    lateinit var scale: IVec3D
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