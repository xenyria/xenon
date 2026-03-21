package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.EditorMode
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

class LocalEditorTarget(
    override val uuid: UUID,
    initialPosition: Vector3d,
    initialRotation: Vector3d,
    initialScale: Vector3d,
    override val supportedModes: Set<EditorMode>,
    override val supportedRotationAxes: Set<Axis>
) : IEditorTarget {
    override var position: Vector3d = initialPosition
    override var rotation: Vector3d = initialRotation
    override var scale: Vector3d = initialScale

    override fun synchronize(position: Vector3dc, rotation: Vector3dc, scale: Vector3dc) {
        this.position = Vector3d(position)
        this.rotation = Vector3d(rotation)
        this.scale = Vector3d(scale)
    }
}