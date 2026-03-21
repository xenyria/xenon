package net.xenyria.xenon.forklift.gizmo

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.TransformationMode
import org.joml.Vector3dc
import java.util.*

interface ILevelEditorGizmo {

    val renderDistance: Int

    val uuid: UUID

    val objectPosition: Vector3dc

    val objectRotation: Vector3dc

    val objectScale: Vector3dc

    val allowedModes: Set<TransformationMode>

    val allowedRotationAxes: Set<Axis>
        get() {
            return setOf(Axis.X, Axis.Y, Axis.Z)
        }
}
