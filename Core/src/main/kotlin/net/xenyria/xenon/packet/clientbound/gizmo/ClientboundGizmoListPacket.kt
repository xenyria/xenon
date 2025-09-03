/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.clientbound.gizmo

import net.xenyria.xenon.core.readList
import net.xenyria.xenon.core.readUUID
import net.xenyria.xenon.core.writeList
import net.xenyria.xenon.core.writeUUID
import net.xenyria.xenon.forklift.gizmo.GizmoData
import net.xenyria.xenon.forklift.gizmo.readGizmo
import net.xenyria.xenon.forklift.gizmo.writeGizmo
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.CLIENTBOUND_GIZMO_LIST
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Sent by the server to synchronize the list of available Gizmos.
 */
class ClientboundGizmoListPacket() : IXenonPacket(CLIENTBOUND_GIZMO_LIST) {

    constructor(updated: List<GizmoData>, added: List<GizmoData>, removed: List<UUID>) : this() {
        this.updated = updated
        this.added = added
        this.removed = removed
    }

    var updated: List<GizmoData> = emptyList()
        private set
    var added: List<GizmoData> = emptyList()
        private set
    var removed: List<UUID> = emptyList()
        private set

    override fun deserialize(input: DataInputStream) {
        updated = input.readList(::readGizmo)
        added = input.readList(::readGizmo)
        removed = input.readList { it.readUUID() }
    }

    override fun serialize(output: DataOutputStream) {
        output.writeList(updated) { writeGizmo(it, output) }
        output.writeList(added) { writeGizmo(it, output) }
        output.writeList(removed) { output.writeUUID(it) }
    }

}