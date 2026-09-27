@file:Suppress("DuplicatedCode")

package net.xenyria.xenon.forklift.editor.state.translate

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.forklift.editor.state.IEditorCommonState
import net.xenyria.xenon.forklift.editor.state.MODIFIERS_COLOR
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.gizmo.AxisTipType
import net.xenyria.xenon.forklift.render.roundToNearestMultiple
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import org.joml.Vector3d
import kotlin.math.abs

private const val DEFAULT_TRANSLATION_SENSITIVITY = 0.0035
private const val DEFAULT_TRANSLATION_FINE_SENSITIVITY = 0.00125

class TranslateState(game: IEditorGameClient, target: IEditorTarget) : IEditorCommonState(game, target) {

    private var currentTargetPosition: Vector3d = Vector3d(0.0)
    override val renderAxisType: AxisTipType = AxisTipType.CONE

    override fun shouldRotateGizmo(): Boolean {
        return false
    }

    fun getSnapValue(): Double {
        return client.forkliftConfig.translationGridSnap
    }

    @Synchronized
    override fun moveByDelta(axis: Axis, displacement: Double) {
        val sensitivity =
            if (client.hasShiftDown()) DEFAULT_TRANSLATION_FINE_SENSITIVITY else DEFAULT_TRANSLATION_SENSITIVITY
        val displacement = displacement * (sensitivity * -1)

        val delta = axis.positive.mul(displacement)
        val newPosition = Vector3d(currentTargetPosition).add(delta)

        if (client.hasControlDown()) {
            target.position = roundToNearestMultiple(newPosition, getSnapValue(), axis)
        } else {
            target.position = newPosition
        }
        currentTargetPosition = newPosition
    }

    private fun appendEditingModifiers(): String {
        val modifiers = ArrayList<String>()
        if (client.hasControlDown()) modifiers.add("Grid")
        if (client.hasShiftDown()) modifiers.add("Fine")
        return if (modifiers.isEmpty()) "" else " (" + modifiers.joinToString(", ") + ")"
    }

    @Synchronized
    override fun beginEdit() {
        currentTargetPosition = target.position
    }

    override val type: EditorMode = EditorMode.TRANSLATE

    @Synchronized
    override fun getStatus(): Message? {
        val axis = getEditingAxis()
        if (client.editorState.isSelected(target.uuid) && axis != null) {
            val effectiveDelta = deltaOf(previousPosition!!, target.position)
            val delta = getVectorComponent(axis, effectiveDelta)
            val sign = delta < 0
            val signStr = if (sign) "-" else "+"

            val components = mutableListOf<MessageComponent>()

            var display: String = signStr + abs(delta).format(2)
            display += " (=" + getVectorComponent(axis, target.position).format(2) + ")"

            components.add(MessageComponent(display, getAxisColor(axis)))
            components.add(MessageComponent(appendEditingModifiers(), MODIFIERS_COLOR))

            return Message(components)
        } else {
            val axis = getSelectedAxis()
            if (axis != null) {
                var str: String = "Translate " + axis.name
                str += " (=" + getVectorComponent(axis, target.position).format(2) + ")"
                return Message(listOf(MessageComponent(str, getAxisColor(axis))))
            }
        }
        return null
    }
}