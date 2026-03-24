package net.xenyria.xenon.forklift.render.gizmo

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.editor.GizmoRotationHelper
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.multiplyColor
import net.xenyria.xenon.forklift.render.primitive.BoxPrimitive
import net.xenyria.xenon.forklift.render.primitive.ConePrimitive
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.forklift.render.sinModifier
import org.joml.Vector3d
import org.joml.Vector3dc
import java.awt.Color

const val MAX_TIP_LENGTH = 0.08
const val TIP_SIZE = 0.002
const val TIP_BOUNDING_BOX_BASE_SIZE = 0.005
const val AXIS_TIP_SIZE = 0.15
const val AXIS_TIP_RADIUS = 0.065
const val AXIS_EDIT_ALPHA = 32

const val AXIS_HIGHLIGHT_PERIOD = 100
const val AXIS_HOVER_MIN_BRIGHTNESS = 0.5
const val AXIS_HOVER_HIGHLIGHT_MIN_VALUE = AXIS_HOVER_MIN_BRIGHTNESS
const val AXIS_SELECTED_HIGHLIGHT_MIN_VALUE = 0.75
const val AXIS_SELECTED_MIN_BRIGHTNESS = 1.5

enum class AxisRenderType {
    CONE, // Translation
    BOX // Scale
}

fun getAxisEditorColor(axis: Axis, isSelected: Boolean, isEditing: Boolean): Color {
    val color = requireNotNull(getAxisColor(axis))
    if (isSelected) {
        val mod = AXIS_HOVER_MIN_BRIGHTNESS + sinModifier(
            AXIS_HIGHLIGHT_PERIOD,
            AXIS_HOVER_HIGHLIGHT_MIN_VALUE
        )
        return multiplyColor(color, mod)
    }
    if (isEditing) {
        val mod = 3.5
        return multiplyColor(color, mod)
    } else {
        return color
    }
}

object DefaultGizmoRenderer {

    fun drawAxisBox(
        renderer: IGameRenderer,
        origin: Vector3dc,
        direction: Vector3dc,
        axis: Axis,
        color: Color,
        axisRotation: Vector3dc
    ) {
        val axisEnd = Vector3d(origin)
        axisEnd.add(Vector3d(direction).mul(AXIS_EDIT_LENGTH + MAX_TIP_LENGTH))

        val box = makeCenteredBox(axisEnd, 0.0, 0.0).grow(MAX_TIP_LENGTH, MAX_TIP_LENGTH, MAX_TIP_LENGTH)
        renderer.drawPrimitives(listOf(BoxPrimitive(box, color, axisRotation)), true)
    }

    private fun drawAxisCone(
        renderer: IGameRenderer,
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

    fun drawGizmo(
        renderer: IGameRenderer, selectedAxis: Axis?, hoveredAxis: Axis?,
        position: Vector3dc, rotation: Vector3dc,
        axisRenderType: AxisRenderType? = null,
        transparent: Boolean
    ) {
        for (axis in Axis.entries) {
            var color: Color = getAxisEditorColor(axis, axis == hoveredAxis, axis == selectedAxis)
            if (transparent) color = Color(color.red, color.green, color.blue, AXIS_EDIT_ALPHA)

            val origin = Vector3d(position)
            val end = GizmoRotationHelper.translateGizmoPosition(origin, axis, rotation)
            val direction = deltaOf(origin, end)

            renderer.drawPrimitives(listOf(LinePrimitive(origin, end, color, 8.0F)), true)
            if (axisRenderType != null) {
                when (axisRenderType) {
                    AxisRenderType.CONE -> drawAxisCone(renderer, position, direction, color)
                    AxisRenderType.BOX -> drawAxisBox(renderer, origin, direction, axis, color, rotation)
                }
            }
        }

    }

}