package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.packet.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.packet.serverbound.state.ServerboundUpdateSelectionPacket
import java.util.*

class TargetManager(val client: IGameClient) {

    // TargetIDs to Player IDs
    private val _activeEditors = HashMap<UUID, UUID>()
    private val _availableTargets = ArrayList<TrackedTarget>()
    private var _selectedGizmoId: UUID? = null
    private var _lastSentGizmoId: UUID? = null
    private var _selectedTarget: TrackedTarget? = null
    private var _currentMode: EditorMode = EditorMode.TRANSLATE

    @Synchronized
    fun updateTarget(target: IEditorTarget) {
        val foundTarget = _availableTargets.find { it.target.uuid == target.uuid } ?: return
        foundTarget.target.synchronize(target.position, target.rotation, target.scale)
    }

    @Synchronized
    fun updateTargets(targets: List<IEditorTarget>) {
        val activeIds = _availableTargets.map { it.target.uuid }.toSet()
        val newIds = targets.map { it.uuid }

        _availableTargets.removeIf { !activeIds.contains(it.target.uuid) }
        for (target in targets) {
            if (!activeIds.contains(target.uuid)) {
                _availableTargets.add(TrackedTarget(client, target, _currentMode))
            }
        }

        val currentTarget = _selectedTarget
        if (currentTarget != null && !newIds.contains(currentTarget.target.uuid)) {
            // Invalidate target if it got removed
            _selectedTarget = null
        }
    }

    @Synchronized
    fun selectMode(mode: EditorMode) {
        _currentMode = mode
        for (target in _availableTargets) target.setMode(mode)
    }

    @Synchronized
    fun updateSelectedGizmo(gizmoId: UUID?) {
        _selectedGizmoId = gizmoId
    }

    @Synchronized
    fun onTick() {
        val newId = _selectedGizmoId
        if (_lastSentGizmoId != newId) {
            _lastSentGizmoId = _selectedGizmoId
            client.sendPacket(ServerboundUpdateSelectionPacket(newId))
        }
    }

    @Synchronized
    fun reset() {
        _availableTargets.clear()
        _selectedTarget = null
        _selectedGizmoId = null
    }

    fun getActiveTarget(): TrackedTarget? {
        return _selectedTarget
    }

    fun getAvailableTargets(): List<TrackedTarget> {
        val list = _availableTargets.toMutableList()
        list.removeIf { !it.target.supportedModes.contains(_currentMode) }
        return list
    }

    fun setActiveTarget(candidate: TrackedTarget) {
        _selectedTarget = candidate
    }

    fun releaseTarget() {
        _selectedTarget ?: return
        _selectedTarget = null
        client.sendPacket(ServerboundReleaseGizmoPacket())
    }

    fun getActiveEditor(targetId: UUID): UUID? {
        return _activeEditors[targetId]
    }

    fun setActiveEditor(targetId: UUID, user: UUID?) {
        if (user != null) {
            _activeEditors[targetId] = user
        } else {
            _activeEditors.remove(targetId)
        }
    }

    fun canEditTarget(targetId: UUID): Boolean {
        return _activeEditors[targetId] == null
    }

    fun getActiveMode(): EditorMode {
        return _currentMode
    }

}