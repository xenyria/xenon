package net.xenyria.xenon.forklift.gizmo

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.state.IEditorState
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import org.joml.Vector2d
import java.awt.Color
import java.util.*

val AXIS_X_COLOR = Color(255, 64, 64)
val AXIS_Y_COLOR = Color(64, 255, 64)
val AXIS_Z_COLOR = Color(64, 64, 255)

const val AXIS_EDIT_LENGTH = 1.0

private val AXIS_COLORS = mapOf(
    Axis.X to AXIS_X_COLOR,
    Axis.Y to AXIS_Y_COLOR,
    Axis.Z to AXIS_Z_COLOR
)

fun getAxisColor(axis: Axis): Color {
    return requireNotNull(AXIS_COLORS[axis]) { "Encountered unknown axis $axis" }
}

class TransformationGizmo(val uuid: UUID, val target: IEditorTarget, val mode: EditorMode) {

    private var _state: IEditorState? = null

    fun handleMouseMovement(delta: Vector2d) {
        synchronized(_mutex) {
            val state = _state ?: return
            state.handleMouseMovement(delta)
        }
    }

    private val _mutex = Any()

}