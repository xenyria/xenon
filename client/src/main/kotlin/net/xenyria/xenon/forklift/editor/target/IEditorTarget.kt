package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.RotationMode
import net.xenyria.xenon.forklift.editor.EditorMode
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.*

/**
 * Represents a generic editor target that can be edited by Forklift.
 */
interface IEditorTarget {

    /**
     * Unique identifier for this target. Must be unique across all available targets.
     */
    val uuid: UUID

    /**
     * Mutable properties for position, scale, rotation.
     */
    var position: Vector3d
    var scale: Vector3d
    var rotation: Vector3d

    /**
     * A set of which editor modes are supported by this target.
     */
    val supportedModes: Set<EditorMode>

    /**
     * The rotation mode of this target. (either euler angles or yaw/pitch)
     */
    val rotationMode: RotationMode

    /**
     * A set of which rotation axes are supported by this target.
     */
    val supportedRotationAxes: Set<Axis>

    /**
     * Syncs external state changes to this target.
     * This is used in cases where we're dealing with remote targets that are being controlled by the server or other
     * clients.
     */
    fun synchronize(position: Vector3dc?, rotation: Vector3dc?, scale: Vector3dc?)

}