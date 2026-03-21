package net.xenyria.xenon.forklift.editor.state

import net.xenyria.xenon.core.*
import net.xenyria.xenon.forklift.editor.GizmoManipulator
import net.xenyria.xenon.forklift.editor.GizmoRotationHelper
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.input.MouseButtonEvent
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.IGameRenderer
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
        val intersectables = ArrayList<IntersectableAxis>()
        for (axis in Axis.entries) {
            val (from, to) = getAxisLine(axis)
            intersectables.add(IntersectableAxis(axis, from, to))
        }

        /**
         * I'm aware that this is extremely stupid to do. Keep in mind that we can only do ray-casts with axis aligned boxes at the moment.
         * The only option I was able to come up with was creating boxes along the axis line and then doing ray-casts
         * to check which axis the user is currently looking at.
         * It's dumb, and I'm aware of that. It's fast & accurate enough to not cause problems at the moment.
         */
        val camera = game.getCamera()
        val result = RayCast.findNearestIntersection(intersectables, camera.position, camera.direction, AXIS_INTERACTION_SIZE)
            ?: return null

        val lowestDistance = result.data.boundingBoxes.minOf { it.center.distance(camera.position) }
        return GizmoAxisIntersection(lowestDistance, result.data.axis)
    }

    @Synchronized
    fun getAxisLine(axis: Axis): Pair<Vector3dc, Vector3dc> {
        val position = Vector3d(target.position)
        if (!shouldRotateGizmo()) {
            val direction = axis.positive
            return Pair(position, Vector3d(position).add(direction.mul(AXIS_LINE_LENGTH)))
        } else {
            val rotation = target.rotation
            val matrix4f = Matrix4f()
            GizmoRotationHelper.applyRotation(matrix4f, rotation)
            matrix4f.translate(axis.positive.x.toFloat(), axis.positive.y.toFloat(), axis.positive.z.toFloat())

            val output = Vector3f()
            matrix4f.getTranslation(output)

            val directionVector = Vector3d(output.x.toDouble(), output.y.toDouble(), output.z.toDouble()).normalize()
            return Pair(position, Vector3d(position).add(directionVector.mul(AXIS_LINE_LENGTH)))
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

        if (selectedAxis != null) game.editor.updateSelectedGizmo(target.uuid)

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