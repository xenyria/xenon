package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.RotationMode
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

/**
 * Represents an editor target that lives on the server.
 * Although we keep a copy of all the target's states, the server acts as the source of truth.
 * Updating requires sending a [ServerboundUpdateGizmoPacket] to the server.
 */
class RemoteEditorTarget(
    val client: IEditorGameClient,
    override val uuid: UUID,
    initialPosition: Vector3dc,
    initialRotation: Vector3dc,
    initialScale: Vector3dc,
    override val supportedModes: Set<EditorMode>,
    override val supportedRotationAxes: Set<Axis>,
    override val rotationMode: RotationMode
) : IEditorTarget {

    private fun emitUpdate() {
        // Since editing a gizmo can end up causing a ton of updates every second, we debounce the updates to avoid
        // spamming the server with packets.
        client.debounceGizmoPacket(ServerboundUpdateGizmoPacket(uuid, position, rotation, scale))
    }

    override var position: Vector3d = Vector3d(initialPosition)
        set(value) {
            field = value
            emitUpdate()
        }

    override var rotation: Vector3d = Vector3d(initialRotation)
        set(value) {
            field = value
            emitUpdate()
        }

    override var scale: Vector3d = Vector3d(initialScale)
        set(value) {
            field = value
            emitUpdate()
        }

    override fun synchronize(position: Vector3dc?, rotation: Vector3dc?, scale: Vector3dc?) {
        if (position != null) this.position.set(Vector3d(position))
        if (rotation != null) this.rotation.set(Vector3d(rotation))
        if (scale != null) this.scale.set(Vector3d(scale))
    }
}