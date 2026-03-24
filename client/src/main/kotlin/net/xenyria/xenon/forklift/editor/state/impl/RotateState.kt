package net.xenyria.xenon.forklift.editor.state.impl

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.format
import net.xenyria.xenon.core.getVectorComponent
import net.xenyria.xenon.core.setVectorComponent
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.GizmoRotationHelper
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.state.*
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.gizmo.getAxisColor
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.gizmo.getAxisEditorColor
import net.xenyria.xenon.forklift.render.multiplyColor
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.forklift.render.primitive.makeRingPrimitive
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import org.joml.Vector2d
import org.joml.Vector3d
import java.awt.Color
import kotlin.math.abs

const val ROTATION_GIZMO_RADIUS = 0.75
const val ROTATION_GIZMO_LINE_WIDTH = 8.0F

class RotateState(game: IGameClient, target: IEditorTarget) : IEditorState(game, target) {

    private val _rotator = GizmoRotator(game, target)

    fun isAxisAvailable(axis: Axis): Boolean {
        return target.supportedRotationAxes.contains(axis)
    }

    override fun render(renderer: IGameRenderer, isSelected: Boolean, isTransparent: Boolean) {
        val hoveringAxis = getSelectedAxis()
        if (!isSelected) _rotator.resetSelectedAxis()

        val alpha = if (isSelected && !isTransparent) 255 else 8

        val editingAxis = _rotator.editingAxis
        if (isAxisAvailable(Axis.Y) && (editingAxis == null || editingAxis === Axis.Y)) {
            // Y axis
            var color = getAxisEditorColor(Axis.Y, hoveringAxis == Axis.Y, _rotator.editingAxis === Axis.Y)
            color = Color(color.red, color.green, color.blue, alpha)
            renderer.drawPrimitives(
                makeRingPrimitive(target.position, ROTATION_GIZMO_RADIUS, color, 0.0F, ROTATION_GIZMO_LINE_WIDTH, false),
                true
            )
        }
        if (isAxisAvailable(Axis.X) && (editingAxis == null || editingAxis === Axis.X)) {
            // X axis
            var color = getAxisEditorColor(Axis.X, hoveringAxis == Axis.X, editingAxis === Axis.X)
            color = Color(color.red, color.green, color.blue, alpha)
            renderer.drawPrimitives(
                makeRingPrimitive(target.position, ROTATION_GIZMO_RADIUS, color, 0.0F, ROTATION_GIZMO_LINE_WIDTH, true),
                true
            )
        }
        if (isAxisAvailable(Axis.Z) && (editingAxis == null || editingAxis === Axis.Z)) {
            // Z axis
            var color = getAxisEditorColor(Axis.Z, hoveringAxis == Axis.Z, editingAxis === Axis.Z)
            color = Color(color.red, color.green, color.blue, alpha)
            renderer.drawPrimitives(
                makeRingPrimitive(target.position, ROTATION_GIZMO_RADIUS, color, 90.0F, ROTATION_GIZMO_LINE_WIDTH, true),
                true
            )
        }

        // Render additional editor indicator
        if (editingAxis != null) {
            val previousRotationValue = _rotator.getPreviousRotation(editingAxis)
            val newRotationValue = _rotator.getNewRotation(editingAxis)

            val offset = when (editingAxis) {
                Axis.X -> Vector3d(0.0, 0.0, ROTATION_GIZMO_RADIUS)
                Axis.Y -> Vector3d(ROTATION_GIZMO_RADIUS, 0.0, 0.0)
                Axis.Z -> Vector3d(ROTATION_GIZMO_RADIUS, 0.0, 0.0)
            }

            val previousRotation = Vector3d()
            setVectorComponent(editingAxis, previousRotation, previousRotationValue)
            val newRotation = Vector3d()
            setVectorComponent(editingAxis, newRotation, newRotationValue)

            val oldRotationPoint = GizmoRotationHelper.transformPosition(previousRotation, offset)
            val newRotationPoint = GizmoRotationHelper.transformPosition(newRotation, offset)

            val center = target.position
            renderer.drawPrimitives(
                listOf(
                    LinePrimitive(
                        center, oldRotationPoint.add(center),
                        multiplyColor(getAxisColor(editingAxis), 0.8),
                        ROTATION_GIZMO_LINE_WIDTH
                    ),
                    LinePrimitive(
                        center, newRotationPoint.add(center),
                        multiplyColor(getAxisColor(editingAxis), 3.5),
                        ROTATION_GIZMO_LINE_WIDTH
                    ),
                ),
                true
            )
        }
    }

    override fun querySelectedAxis(): GizmoAxisIntersection? {
        return _rotator.querySelectedAxis()
    }

    override fun onInteract(event: MouseButtonEvent): GizmoInteractionResult {
        return _rotator.onInteract(event)
    }

    override fun handleMouseMovement(movement: Vector2d) {
        _rotator.onMouseMove(game, movement)
    }

    override val type: EditorMode = EditorMode.ROTATE

    private fun appendEditingModifiers(): String {
        val modifiers = ArrayList<String>()
        if (game.hasControlDown()) modifiers.add("Grid")
        if (game.hasShiftDown()) modifiers.add("Fine")
        return if (modifiers.isEmpty()) "" else " (" + java.lang.String.join(", ", modifiers) + ")"
    }

    override fun getStatus(): Message? {
        val axis = _rotator.editingAxis
        if (game.editor.isSelected(target.uuid) && axis != null) {
            val effectiveDelta = _rotator.getEffectiveRotation()
            val delta: Double = when (axis) {
                Axis.X -> effectiveDelta.x
                Axis.Y -> effectiveDelta.y
                Axis.Z -> effectiveDelta.z
            }
            val sign = delta < 0
            val signStr = if (sign) "-" else "+"
            var displayString: String = signStr + (abs(delta)).format(2)
            displayString += " (=" + getVectorComponent(axis, target.rotation).format(2) + ")"
            displayString += " "

            val firstComponent = MessageComponent(displayString, getAxisColor(axis))
            return Message(listOf(firstComponent, MessageComponent(appendEditingModifiers(), MODIFIERS_COLOR)))
        } else {
            val axis = getSelectedAxis()
            if (axis != null) {
                var displayString = "Rotate " + axis.name
                displayString += " (=" + getVectorComponent(axis, target.rotation).format(2) + ")"
                return Message(listOf(MessageComponent(displayString, getAxisColor(axis))))
            }
        }
        return null
    }
}