/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.forklift.gizmo

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.TransformationMode
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Represents a locally tracked server Gizmo
 */
data class GizmoData(
    val gizmoId: UUID,
    val editorId: UUID?,
    val position: IVec3D,
    val rotation: IVec3D,
    val scale: IVec3D,
    val rotationAxes: Set<Axis>,
    val allowedModes: Set<TransformationMode>
)

fun writeGizmo(gizmoData: GizmoData, output: DataOutputStream) {
    output.writeUUID(gizmoData.gizmoId)
    output.writeOptional(gizmoData.editorId, output::writeUUID)
    output.writeVec3D(gizmoData.position)
    output.writeVec3D(gizmoData.rotation)
    output.writeVec3D(gizmoData.scale)
    output.writeSet(gizmoData.rotationAxes) { output.writeByte(it.ordinal) }
    output.writeSet(gizmoData.allowedModes) { output.writeByte(it.ordinal) }
}

fun readGizmo(input: DataInputStream): GizmoData {
    return GizmoData(
        input.readUUID(),
        input.readOptional { it.readUUID() },
        input.readVec3D(),
        input.readVec3D(),
        input.readVec3D(),
        input.readSet { Axis.entries[it.readByte().toInt()] },
        input.readSet { TransformationMode.entries[it.readByte().toInt()] }
    )
}
