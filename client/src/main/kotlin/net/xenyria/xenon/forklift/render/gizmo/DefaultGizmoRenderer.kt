package net.xenyria.xenon.forklift.render.gizmo

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.editor.GizmoRotationHelper
import net.xenyria.xenon.forklift.editor.state.AXIS_TIP_SIZE
import net.xenyria.xenon.forklift.render.IGameRenderContext
import net.xenyria.xenon.forklift.render.multiplyColor
import net.xenyria.xenon.forklift.render.primitive.BoxPrimitive
import net.xenyria.xenon.forklift.render.primitive.ConePrimitive
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.forklift.render.sinModifier
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

const val MAX_TIP_LENGTH = 0.08
const val AXIS_TIP_RADIUS = 0.065
const val AXIS_EDIT_ALPHA = 32

const val AXIS_HIGHLIGHT_PERIOD = 100 // Time in millis for a full sine wave cycle for the axis highlight effect
const val AXIS_HOVER_MIN_BRIGHTNESS = 0.5 // Minimum brightness multiplier for the axis highlight effect when hovered
const val AXIS_HOVER_HIGHLIGHT_MIN_VALUE = 0.5 // Boundary value for the sine wave modifier

/**
 * Enum representing the different types of axis tips that can be rendered for a gizmo.
 */
enum class AxisTipType {
    CONE, BOX
}

/**
 * Returns the color for the given axis and editing state.
 * Selected axes use a sine wave modifier to create a pulsing effect. (which is turned off when editing)
 */
fun getAxisEditorColor(axis: Axis, isSelected: Boolean, isEditing: Boolean): Color {
    val color = requireNotNull(getAxisColor(axis))
    if (isSelected) {
        val mod = AXIS_HOVER_MIN_BRIGHTNESS + sinModifier(AXIS_HIGHLIGHT_PERIOD, AXIS_HOVER_HIGHLIGHT_MIN_VALUE)
        return multiplyColor(color, mod)
    }
    if (isEditing) {
        val mod = 3.5
        return multiplyColor(color, mod)
    } else {
        return color
    }
}

/**
 * Helper functions for rendering gizmos in the editor.
 */
object DefaultGizmoRenderer {

    /**
     * Renders a box at the end of the axis to represent the tip of the gizmo.
     */
    fun drawAxisBox(
        renderer: IGameRenderContext,
        origin: Vector3dc,
        direction: Vector3dc,
        color: Color,
        axisRotation: Vector3dc
    ) {
        val axisEnd = Vector3d(origin)
        axisEnd.add(Vector3d(direction).mul(AXIS_EDIT_LENGTH + MAX_TIP_LENGTH))

        val box = makeCenteredBox(axisEnd, 0.0, 0.0).grow(MAX_TIP_LENGTH, MAX_TIP_LENGTH, MAX_TIP_LENGTH)
        renderer.drawPrimitives(listOf(BoxPrimitive(box, color, axisRotation)), true)
    }

    /**
     * Renders a cone at the end of the axis to represent the tip of the gizmo.
     */
    private fun drawAxisCone(
        renderer: IGameRenderContext,
        position: Vector3dc,
        direction: Vector3dc,
        color: Color
    ) {
        val start = Vector3d(position).add(Vector3d(direction).mul(AXIS_EDIT_LENGTH))
        val end = Vector3d(position).add(Vector3d(direction).mul(AXIS_EDIT_LENGTH + AXIS_TIP_SIZE))
        renderer.drawPrimitives(
            listOf(ConePrimitive(end, start, color, AXIS_TIP_RADIUS)),
            true
        )
    }

    /**
     * Draws a gizmo with the given parameters.
     * The gizmo consists of one line per axis + an optional tip at the end of each axis.
     */
    fun drawGizmo(
        renderer: IGameRenderContext, selectedAxis: Axis?, hoveredAxis: Axis?,
        position: Vector3dc, rotation: Vector3dc,
        axisTip: AxisTipType? = null,
        transparent: Boolean
    ) {
        for (axis in Axis.entries) {
            var color: Color = getAxisEditorColor(axis, axis == hoveredAxis, axis == selectedAxis)
            if (transparent) color = Color(color.red, color.green, color.blue, AXIS_EDIT_ALPHA)

            val origin = Vector3d(position)
            val end = GizmoRotationHelper.translateGizmoPosition(origin, axis, rotation)
            val direction = deltaOf(origin, end)

            renderer.drawPrimitives(listOf(LinePrimitive(origin, end, color, 8.0F)), true)
            if (axisTip != null) {
                when (axisTip) {
                    AxisTipType.CONE -> drawAxisCone(renderer, position, direction, color)
                    AxisTipType.BOX -> drawAxisBox(renderer, origin, direction, color, rotation)
                }
            }
        }
    }

}