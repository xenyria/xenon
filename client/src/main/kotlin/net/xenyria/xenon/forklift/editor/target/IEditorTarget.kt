package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.EditorMode
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

interface IEditorTarget {

    val uuid: UUID

    var position: Vector3d

    var scale: Vector3d

    var rotation: Vector3d

    val supportedModes: Set<EditorMode>

    val supportedRotationAxes: Set<Axis>

    fun synchronize(position: Vector3dc, rotation: Vector3dc, scale: Vector3dc)

}