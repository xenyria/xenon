package net.xenyria.xenon.forklift.editor.state

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.editor.GizmoManipulator
import net.xenyria.xenon.forklift.editor.GizmoRotationHelper
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.IGameRenderer
import net.xenyria.xenon.forklift.render.gizmo.AXIS_TIP_SIZE
import net.xenyria.xenon.forklift.render.gizmo.AxisRenderType
import net.xenyria.xenon.forklift.render.gizmo.DefaultGizmoRenderer
import org.joml.*
import java.awt.Color

val MODIFIERS_COLOR = Color(232, 169, 51)
const val VISUAL_AXIS_SIZE_MODIFIER = 0.1
const val AXIS_INTERACTION_SIZE = 7.0
const val AXIS_LINE_LENGTH = 1.0
const val EDIT_AXIS_LENGTH = 64.0
const val AXIS_INTERACTION_RANGE = 7.0

private data class EditingSession(val axis: Axis)

abstract class IEditorCommonState(game: IGameClient, target: IEditorTarget) : IEditorState(game, target) {

    private val _gizmoManipulator = GizmoManipulator

    // Previous position & scale before the edit was requested
    protected var previousPosition: Vector3dc? = null
    protected var previousScale: Vector3dc? = null

    private var _editingSession: EditingSession? = null

    abstract val renderAxisType: AxisRenderType

    abstract fun shouldRotateGizmo(): Boolean

    @Synchronized
    override fun handleMouseMovement(movement: Vector2d) {
        val session = _editingSession ?: return
        val (start, end) = getAxisLine(session.axis)

        val direction = directionOf(start, end)
        val delta = _gizmoManipulator.calculateMovementDelta(game, session.axis, direction, target)
        handleDelta(delta.axis, delta.displacement)
    }

    /**
     * Allows the underlying implementation to work with the user's input.
     */
    abstract fun handleDelta(axis: Axis, displacement: Double)

    @Synchronized
    override fun onInteract(event: MouseButtonEvent): GizmoInteractionResult {
        if (!event.isRightMouseButton) return GizmoInteractionResult.NONE
        if (event.isReleased) {
            _editingSession = null
            _gizmoManipulator.reset()
            return GizmoInteractionResult.END_EDIT
        } else if (event.isPressed) {
            val query = querySelectedAxis()
            if (query != null) {
                _editingSession = EditingSession(query.axis)
                _gizmoManipulator.reset()
                previousPosition = Vector3d(target.position)
                previousScale = Vector3d(target.scale)
                game.editor.enableDragMode(target.uuid)
                beginEdit()
                return GizmoInteractionResult.START_EDIT
            }
        }
        return GizmoInteractionResult.NONE
    }

    abstract fun beginEdit()

    @Synchronized
    override fun querySelectedAxis(): GizmoAxisIntersection? {
        val intersectables = ArrayList<Pair<Axis, List<OBB>>>()
        for (axis in Axis.entries) {
            val (from, to) = getAxisLine(axis)
            val center = (Vector3d(from).add(to)).div(2.0)
            val tip = Vector3d(to)

            val thin = .08
            val lineBox = OBB(
                center,
                if (axis == Axis.X) 1.0 else thin,
                if (axis == Axis.Y) 1.0 else thin,
                if (axis == Axis.Z) 1.0 else thin,
            )
            val tipBox = OBB(tip, AXIS_TIP_SIZE, AXIS_TIP_SIZE, AXIS_TIP_SIZE)

            val matrix = Matrix4d()
            if (shouldRotateGizmo()) GizmoRotationHelper.applyRotation(matrix, target.rotation)

            lineBox.setOrientation(
                matrix.transformPosition(Vector3d(1.0, 0.0, 0.0)),
                matrix.transformPosition(Vector3d(0.0, 1.0, 0.0)),
                matrix.transformPosition(Vector3d(0.0, 0.0, 1.0)),
            )
            tipBox.setOrientation(
                matrix.transformPosition(Vector3d(1.0, 0.0, 0.0)),
                matrix.transformPosition(Vector3d(0.0, 1.0, 0.0)),
                matrix.transformPosition(Vector3d(0.0, 0.0, 1.0)),
            )

            intersectables.add(axis to listOf(lineBox, tipBox))
        }

        val camera = game.getCamera()
        val results = ArrayList<Pair<Axis, Double>>()
        for ((axis, boxes) in intersectables) {
            val axisResults = ArrayList<Double>()
            for (box in boxes) {
                val result = box.intersection(camera.position, camera.direction) ?: continue
                val distance = result.distance(camera.position)
                axisResults.add(distance)
            }
            if (axisResults.isEmpty()) continue
            results.add(axis to axisResults.min())
        }
        if (results.isEmpty()) return null

        val (axis, distance) = results.minBy { it.second }
        return GizmoAxisIntersection(distance, axis)
    }

    @Synchronized
    fun getAxisLine(axis: Axis): Pair<Vector3dc, Vector3dc> {
        val position = Vector3d(target.position)
        if (!shouldRotateGizmo()) {
            val direction = axis.positive
            return Pair(position, Vector3d(position).add(direction.mul(AXIS_LINE_LENGTH + (AXIS_TIP_SIZE / 2.0))))
        } else {
            val rotation = target.rotation
            val matrix4f = Matrix4f()
            GizmoRotationHelper.applyRotation(matrix4f, rotation)
            matrix4f.translate(axis.positive.x.toFloat(), axis.positive.y.toFloat(), axis.positive.z.toFloat())

            val output = Vector3f()
            matrix4f.getTranslation(output)

            val directionVector = Vector3d(output.x.toDouble(), output.y.toDouble(), output.z.toDouble()).normalize()
            return Pair(position, Vector3d(position).add(directionVector.mul(AXIS_LINE_LENGTH + (AXIS_TIP_SIZE / 2.0))))
        }
    }

    @Synchronized
    protected fun getEditingAxis(): Axis? {
        return _editingSession?.axis
    }

    @Synchronized
    override fun render(renderer: IGameRenderer, selected: Boolean, index: Int) {
        if (!selected) _editingSession = null

        var selectedAxis: Axis? = getSelectedAxis()
        val editingAxis = getEditingAxis()
        if (editingAxis != null) selectedAxis = null

        DefaultGizmoRenderer.drawGizmo(
            renderer, editingAxis, selectedAxis,
            target.position,
            if (shouldRotateGizmo()) target.rotation else Vector3d(),
            renderAxisType,
            index != 0
        )
    }
}

class IntersectableAxis(val axis: Axis, start: Vector3dc, end: Vector3dc) : IIntersectable {

    override val boundingBoxes: List<Box>

    init {
        val resolution = 10
        var direction = deltaOf(start, end)
        val length: Double = direction.length()
        direction = direction.normalize()

        val bbList = ArrayList<Box>()
        for (i in 0..<resolution) {
            val center = Vector3d(start).add(Vector3d(direction).mul(length * i / resolution))
            val box = Box(center, center)
                .grow(VISUAL_AXIS_SIZE_MODIFIER, VISUAL_AXIS_SIZE_MODIFIER, VISUAL_AXIS_SIZE_MODIFIER)
            bbList.add(box)
        }
        this.boundingBoxes = bbList
    }

}