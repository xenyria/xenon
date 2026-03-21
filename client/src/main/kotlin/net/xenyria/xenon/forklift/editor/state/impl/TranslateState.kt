package net.xenyria.xenon.forklift.editor.state.impl

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.deltaOf
import net.xenyria.xenon.core.format
import net.xenyria.xenon.core.getVectorComponent
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.state.IEditorCommonState
import net.xenyria.xenon.forklift.editor.state.MODIFIERS_COLOR
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.gizmo.getAxisColor
import net.xenyria.xenon.forklift.render.gizmo.AxisRenderType
import net.xenyria.xenon.forklift.render.roundToNearestMultiple
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import org.joml.Vector3d
import kotlin.math.abs

private const val DEFAULT_TRANSLATION_SENSITIVITY = 0.005
private const val DEFAULT_TRANSLATION_FINE_SENSITIVITY = 0.00125

class TranslateState(game: IGameClient, target: IEditorTarget) : IEditorCommonState(game, target) {

    private var _currentTargetPosition: Vector3d = Vector3d(0.0)
    override val renderAxisType: AxisRenderType = AxisRenderType.CONE

    override fun shouldRotateGizmo(): Boolean {
        return false
    }

    fun getSnapValue(client: IGameClient): Double {
        return client.config.translationGridSnap
    }

    @Synchronized
    override fun handleDelta(axis: Axis, displacement: Double) {
        val sensitivity = if (game.hasShiftDown()) DEFAULT_TRANSLATION_SENSITIVITY else DEFAULT_TRANSLATION_FINE_SENSITIVITY
        val displacement = displacement * (sensitivity * -1)

        val delta = axis.positive.mul(displacement)
        var newPosition = Vector3d(_currentTargetPosition).add(delta)

        if (game.hasControlDown()) {
            newPosition = roundToNearestMultiple(newPosition, getSnapValue(game), axis)
        }
        _currentTargetPosition = newPosition
        target.position = newPosition
    }

    private fun appendEditingModifiers(): String {
        val modifiers = ArrayList<String>()
        if (game.hasControlDown()) modifiers.add("Grid")
        if (game.hasShiftDown()) modifiers.add("Fine")
        return if (modifiers.isEmpty()) "" else " (" + modifiers.joinToString(", ") + ")"
    }

    @Synchronized
    override fun beginEdit() {
        _currentTargetPosition = target.position
    }

    override val type: EditorMode = EditorMode.TRANSLATE

    @Synchronized
    override fun getStatus(): Message {
        val axis = getEditingAxis()
        if (game.editor.isSelected(target.uuid) && axis != null) {
            val effectiveDelta = deltaOf(target.position, previousPosition!!)
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
                str += " (=" + getVectorComponent(axis, target.position) + ")"
                return Message(listOf(MessageComponent(str, getAxisColor(axis))))
            }
        }
        return Message.EMPTY
    }
}