package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.RotationMode
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

class RemoteEditorTarget(
    val client: IGameClient,
    override val uuid: UUID,
    initialPosition: Vector3dc,
    initialRotation: Vector3dc,
    initialScale: Vector3dc,
    override val supportedModes: Set<EditorMode>,
    override val supportedRotationAxes: Set<Axis>,
    override val rotationMode: RotationMode
) : IEditorTarget {

    private fun emitUpdate() {
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