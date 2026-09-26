package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import org.joml.Vector2d
import java.util.*

class EditorDragHandler(private val client: IGameClient) {

    private var accumulatedDragDeltaX: Double = 0.0
    private var accumulatedDragDeltaY: Double = 0.0
    private var dragStartMouseX: Double = 0.0
    private var dragStartMouseY: Double = 0.0

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
     * Returns the total mouse movement delta since the last time [enableDragMode] was called.
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

    @Synchronized
    fun getTotalDragDelta(): Vector2d {
        if (!isDragActive) return Vector2d()
        return Vector2d(
            dragStartMouseX + accumulatedDragDeltaX,
            dragStartMouseY + accumulatedDragDeltaY
        )
    }

    @Synchronized
    fun resetDragCenter() {
        if (!isDragActive) return
        val total = getTotalDragDelta()
        dragStartMouseX = total.x
        dragStartMouseY = total.y
        accumulatedDragDeltaX = 0.0
        accumulatedDragDeltaY = 0.0
    }

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

    fun getMousePositionBeforeDrag(): Vector2d {
        return Vector2d(dragStartMouseX, dragStartMouseY)
    }

    private var isDragActive = false

}
