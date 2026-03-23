package net.xenyria.xenon.forklift.editor

import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import org.joml.Vector2d
import java.util.*

class EditorDragHandler(private val client: IGameClient) {

    private var _originMouseX: Double = 0.0
    private var _originMouseY: Double = 0.0
    private var _lastMouseX: Double = 0.0
    private var _lastMouseY: Double = 0.0

    fun isActive(): Boolean {
        return _isDragActive
    }

    @Synchronized
    fun reset() {
        _isDragActive = false
    }

    @Synchronized
    fun exitDragMode() {
        if (!_isDragActive) return
        _isDragActive = false
        client.sendPacket(ServerboundReleaseGizmoPacket())
    }

    /**
     * Returns the mouse movement delta since the last call was made.
     *
     * @param newX The new mouse X position.
     * @param newY The new mouse Y position.
     * @return The mouse movement delta.
     */
    @Synchronized
    fun getMouseMovementDelta(newX: Double, newY: Double): Vector2d {
        if (!_isDragActive) return Vector2d()

        val deltaX: Double = newX - _originMouseX
        val deltaY: Double = newY - _originMouseY

        _originMouseX = newX
        _originMouseY = newY

        return Vector2d(deltaX, deltaY)
    }

    @Synchronized
    fun enableDragMode(gizmo: UUID) {
        if (_isDragActive) return
        _isDragActive = true

        val mousePos = client.getMousePosition()
        _originMouseX = mousePos.x
        _originMouseY = mousePos.y
        _lastMouseX = _originMouseX
        _lastMouseY = _originMouseY

        client.sendPacket(ServerboundRequestGizmoPacket(gizmo))
    }

    fun getMousePositionBeforeDrag(): Vector2d {
        return Vector2d(_originMouseX, _originMouseY)
    }

    private var _isDragActive = false
}
