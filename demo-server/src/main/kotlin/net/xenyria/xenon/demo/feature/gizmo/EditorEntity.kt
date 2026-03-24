package net.xenyria.xenon.demo.feature.gizmo

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.RotationMode
import net.xenyria.xenon.forklift.TransformationMode
import net.xenyria.xenon.forklift.gizmo.GizmoData
import org.joml.Vector3dc
import java.util.*

class EditorEntity(
    private val removeEntity: () -> Unit,
    private val positionGetter: () -> Vector3dc,
    private val positionSetter: (Vector3dc) -> Unit,
    private val rotationGetter: () -> Vector3dc,
    private val rotationSetter: (Vector3dc) -> Unit,
    private val scaleGetter: () -> Vector3dc,
    private val scaleSetter: (Vector3dc) -> Unit,
    val rotationAxes: Set<Axis> = Axis.entries.toSet(),
    val allowedModes: Set<TransformationMode> = TransformationMode.entries.toSet(),
    val rotationMode: RotationMode
) {

    fun setPosition(position: Vector3dc) {
        positionSetter(position)
        XenonGizmos.emitUpdate(this)
    }

    fun setRotation(rotation: Vector3dc) {
        rotationSetter(rotation)
        XenonGizmos.emitUpdate(this)
    }

    fun setScale(position: Vector3dc) {
        scaleSetter(position)
        XenonGizmos.emitUpdate(this)
    }

    val position: Vector3dc get() = positionGetter()
    val scale: Vector3dc get() = scaleGetter()
    val rotation: Vector3dc get() = rotationGetter()

    val id: UUID = UUID.randomUUID()

    fun toGizmoData(): GizmoData {
        return GizmoData(
            id,
            XenonGizmos.getEditor(id),
            positionGetter(),
            rotationGetter(),
            scaleGetter(),
            rotationAxes,
            allowedModes,
            rotationMode
        )
    }

    fun onRemove() {
        removeEntity()
    }

    fun update(position: Vector3dc, scale: Vector3dc, rotation: Vector3dc) {
        positionSetter(position)
        rotationSetter(rotation)
        scaleSetter(scale)
        XenonGizmos.emitUpdate(this)
    }

}