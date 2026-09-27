package net.xenyria.xenon.forklift.gizmo

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.TransformationMode
import org.joml.Vector3dc
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

/**
 * Represents a locally tracked Gizmo/editor target.
 */
data class GizmoData(
    // Unique ID of the gizmo.
    val gizmoId: UUID,
    // ID of the user that is currently editing this target, or null if no one is editing it.
    val editorId: UUID?,
    // Position, rotation and scale
    val position: Vector3dc,
    val rotation: Vector3dc,
    val scale: Vector3dc,
    // The axes that the gizmo can rotate around.
    val rotationAxes: Set<Axis>,
    // The transformation modes that can be applied to the gizmo.
    val allowedModes: Set<TransformationMode>,
    // The supported rotation mode for the gizmo (Euler or Yaw+Pitch)
    val rotationMode: RotationMode
) {
    companion object {
        fun writeGizmo(gizmoData: GizmoData, output: DataOutputStream) {
            output.writeUUID(gizmoData.gizmoId)
            output.writeOptional(gizmoData.editorId, output::writeUUID)
            output.writeVec3D(gizmoData.position)
            output.writeVec3D(gizmoData.rotation)
            output.writeVec3D(gizmoData.scale)
            output.writeSet(gizmoData.rotationAxes) { output.writeByte(it.ordinal) }
            output.writeSet(gizmoData.allowedModes) { output.writeByte(it.ordinal) }
            output.writeByte(gizmoData.rotationMode.ordinal)
        }

        fun readGizmo(input: DataInputStream): GizmoData {
            return GizmoData(
                input.readUUID(),
                input.readOptional { it.readUUID() },
                input.readVec3D(),
                input.readVec3D(),
                input.readVec3D(),
                input.readSet { Axis.entries[it.readByte().toInt()] },
                input.readSet { TransformationMode.entries[it.readByte().toInt()] },
                RotationMode.entries[input.readByte().toInt()]
            )
        }
    }
}
