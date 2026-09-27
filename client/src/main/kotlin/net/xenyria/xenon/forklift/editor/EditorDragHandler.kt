package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import org.joml.Vector2d
import java.util.*

/**
 * Handles mouse dragging events in edit mode.
 */
class EditorDragHandler(private val client: IEditorGameClient) {

    /**
     * Since Minecraft switched to SDL, mouse positions reported by received mouse events are clamped to the window size.
     * This means if a user starts dragging their mouse and moves it really far we will only receive coordinates that
     * are within the window's boundaries.
     *
     * To work around this, we essentially create a "virtual mouse position". When a drag starts, we capture the mouse
     * position and then accumulate relative movement deltas from mouse events. This restores the original behavior of
     * mouse events back when GLFW was used.
     */
    private var accumulatedDragDeltaX: Double = 0.0
    private var accumulatedDragDeltaY: Double = 0.0
    private var dragStartMouseX: Double = 0.0
    private var dragStartMouseY: Double = 0.0

    private var isDragActive = false

    fun isActive(): Boolean {
        return isDragActive
    }

    @Synchronized
    fun reset() {
        isDragActive = false
    }

    @Synchronized
    fun exitDragMode() {
        if (!isDragActive) return
        isDragActive = false
        client.sendPacket(ServerboundReleaseGizmoPacket())
    }

    /**
     * Adds the given relative mouse movement delta to the accumulated drag delta and returns the total mouse movement
     * delta since the last time [enableDragMode] was called.
     *
     * @param relX Movement delta on the X axis.
     * @param relY Movement delta on the Y axis.
     * @return The mouse movement delta.
     */
    @Synchronized
    fun getDragMouseDelta(relX: Double, relY: Double): Vector2d {
        if (!isDragActive) return Vector2d()

        accumulatedDragDeltaX += relX
        accumulatedDragDeltaY += relY

        return Vector2d(
            dragStartMouseX + accumulatedDragDeltaX,
            dragStartMouseY + accumulatedDragDeltaY
        )
    }

    /**
     * Reports the total mouse movement delta since the last time [enableDragMode] was called,
     * without modifying the internal state.
     */
    @Synchronized
    fun getTotalDragDelta(): Vector2d {
        if (!isDragActive) return Vector2d()
        return Vector2d(
            dragStartMouseX + accumulatedDragDeltaX,
            dragStartMouseY + accumulatedDragDeltaY
        )
    }

    /**
     * Resets the drag center to the current mouse position.
     */
    @Synchronized
    fun resetDragCenter() {
        if (!isDragActive) return
        val total = getTotalDragDelta()
        dragStartMouseX = total.x
        dragStartMouseY = total.y
        accumulatedDragDeltaX = 0.0
        accumulatedDragDeltaY = 0.0
    }

    /**
     * Enables drag mode and notifies the server that the user has started interacting with a gizmo.
     */
    @Synchronized
    fun enableDragMode(gizmo: UUID) {
        if (isDragActive) return
        isDragActive = true

        val mousePos = client.getMousePosition()
        dragStartMouseX = mousePos.x
        dragStartMouseY = mousePos.y
        accumulatedDragDeltaX = 0.0
        accumulatedDragDeltaY = 0.0

        client.sendPacket(ServerboundRequestGizmoPacket(gizmo))
    }

    /**
     * Returns the mouse position when the drag started, without any accumulated deltas.
     */
    fun getMousePositionBeforeDrag(): Vector2d {
        return Vector2d(dragStartMouseX, dragStartMouseY)
    }

}
