package net.xenyria.xenon.forklift.editor.state.scale

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.state.IEditorCommonState
import net.xenyria.xenon.forklift.editor.state.MODIFIERS_COLOR
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.gizmo.AxisRenderType
import net.xenyria.xenon.forklift.render.roundToNearestMultiple
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.abs

const val DEFAULT_SCALE_SENSITIVITY = 0.005
const val DEFAULT_SCALE_SHIFT_SENSITIVITY = 0.00125

class ScaleState(game: IGameClient, target: IEditorTarget) : IEditorCommonState(game, target) {

    override val renderAxisType: AxisRenderType = AxisRenderType.BOX
    private var initialScaleValue: Vector3dc = Vector3d(0.0)

    fun getSnapValue(): Double {
        return game.forkliftConfig.scaleGridSnap
    }

    override fun beginEdit() {
        initialScaleValue = Vector3d(target.scale)
    }

    override fun shouldRotateGizmo(): Boolean {
        return true
    }

    @Synchronized
    private fun appendEditingModifiers(): String {
        val modifiers = ArrayList<String>()
        if (game.hasControlDown()) modifiers.add("Grid")
        if (game.hasShiftDown()) modifiers.add("Fine")
        if (game.hasAltDown()) modifiers.add("Combined")
        return if (modifiers.isEmpty()) "" else " (" + modifiers.joinToString(", ") + ")"
    }

    @Synchronized
    override fun handleDelta(axis: Axis, displacement: Double) {
        var displacement = displacement
        val sensitivity: Double = if (game.hasShiftDown()) DEFAULT_SCALE_SHIFT_SENSITIVITY else DEFAULT_SCALE_SENSITIVITY
        displacement *= sensitivity * -1

        var newScale: Vector3dc = Vector3d(initialScaleValue)
        if (game.hasAltDown()) {
            newScale = Vector3d(newScale).add(displacement, displacement, displacement)
            // Combined scaling
            if (game.hasControlDown()) {
                // Snap to grid
                target.scale = roundToNearestMultiple(newScale, getSnapValue())
            } else {
                target.scale = Vector3d(newScale)
            }
            initialScaleValue = Vector3d(newScale)
        } else {
            // Single axis scaling
            newScale = Vector3d(newScale).add(axis.positive.mul(displacement))
            if (game.hasControlDown()) {
                // Snap to grid
                target.scale = roundToNearestMultiple(newScale, getSnapValue(), axis)
            } else {
                target.scale = newScale
            }
            initialScaleValue = Vector3d(newScale)
        }
    }

    override val type: EditorMode = EditorMode.SCALE

    @Synchronized
    override fun getStatus(): Message? {
        val axis = getEditingAxis()
        if (game.editor.isSelected(target.uuid) && axis != null) {
            val effectiveDelta = deltaOf(target.scale, previousScale!!)
            val delta = getVectorComponent(axis, effectiveDelta)
            val sign = delta < 0
            val signStr = if (sign) "-" else "+"

            val components = mutableListOf<MessageComponent>()

            var display = signStr + abs(delta).format(2)
            display += " (=" + getVectorComponent(axis, target.scale).format(2) + ")"

            components.add(MessageComponent(display, getAxisColor(axis)))
            components.add(MessageComponent(appendEditingModifiers(), MODIFIERS_COLOR))

            return Message(components)
        } else {
            val axis = getSelectedAxis()
            if (axis != null) {
                var str = "Scale " + axis.name
                str += " (=" + getVectorComponent(axis, target.scale).format(2) + ")"
                return Message(listOf(MessageComponent(str, getAxisColor(axis))))
            }
        }
        return null
    }

}