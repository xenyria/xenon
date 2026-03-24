package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.directionOf
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.gizmo.GizmoData
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundUpdateSelectionPacket
import java.util.*

class TargetManager(val client: IGameClient) {

    // Target IDs to Player IDs (this is tracked to prevent multiple players from editing the same entity)
    private val _activeEditors = HashMap<UUID, UUID>()
    private val _availableTargets = ArrayList<TrackedTarget>()

    // Used for selection logic (which target the player is currently looking at)
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
    private fun findSelectedGizmo(): TrackedTarget? {
        return findSelectedGizmo(getSortedTargets())
    }

    @Synchronized
    private fun findSelectedGizmo(targets: List<TrackedTarget>): TrackedTarget? {
        val selectedTarget = _selectedTarget
        if (selectedTarget != null) return selectedTarget // Prioritize the entity we're currently editing

        val results = ArrayList<Pair<Double, TrackedTarget>>()
        for (gizmo in targets) {
            if (!gizmo.supportsCurrentMode()) continue
            val state = gizmo.querySelectionState() ?: continue
            results.add(state.distance to gizmo)
        }

        if (results.isNotEmpty()) {
            val (_, target) = results.minBy { it.first }
            return target
        }
        return null
    }

    @Synchronized
    fun onTick() {
        val newId = findSelectedGizmo()?.target?.uuid
        if (_lastSentGizmoId != newId) {
            _lastSentGizmoId = newId
            client.sendPacket(ServerboundUpdateSelectionPacket(newId))
        }
        renderGizmos()
    }

    private fun isInFieldOfView(target: IEditorTarget): Boolean {
        return target == _selectedTarget || targetDot(target) >= 0.8
    }

    private fun targetDot(target: IEditorTarget): Double {
        val targetPosition = target.position
        val cameraPos = client.getCamera().position
        val cameraDir = client.getCamera().direction

        val cameraToGizmoDirection = directionOf(cameraPos, targetPosition)
        return cameraDir.dot(cameraToGizmoDirection)
    }

    @Synchronized
    fun renderGizmos() {
        val targets = getSortedTargets()

        val activeId = getActiveTarget()?.target?.uuid
        val selectedGizmo = findSelectedGizmo()

        val renderList = ArrayList<RenderableGizmo>()
        var index = 0

        for (entry in targets) {
            if (!isInFieldOfView(entry.target)) continue
            val editorPlayer = getActiveEditor(entry.target.uuid)

            if (editorPlayer != null && editorPlayer != client.getPlayerId()) continue
            val error = entry.getErrorMessage()

            // Render the nearest target fully opaque.
            // If we're directly looking at an entity, render it fully opaque instead.
            var isTransparent = index != 0
            if (selectedGizmo != null)
                isTransparent = selectedGizmo.target.uuid != entry.target.uuid

            renderList.add(
                RenderableGizmo(
                    entry,
                    activeId != null && entry.target.uuid == activeId,
                    isTransparent,
                    error
                )
            )
            index++
        }
        client.renderGizmos(renderList)
    }

    @Synchronized
    fun reset() {
        _availableTargets.clear()
        releaseTarget()
        _selectedGizmoId = null
    }

    @Synchronized
    fun getActiveTarget(): TrackedTarget? {
        return _selectedTarget
    }

    @Synchronized
    fun getSortedTargets(): List<TrackedTarget> {
        val targets = getAvailableTargets().toMutableList()
        targets.sortByDescending { targetDot(it.target) }
        val selected = _selectedTarget
        if (selected != null) {
            targets.remove(selected)
            targets.add(0, selected)
        } else {
            val entityInLineOfSight = findSelectedGizmo(targets)
            if (entityInLineOfSight != null) {
                targets.remove(entityInLineOfSight)
                targets.add(0, entityInLineOfSight)
            }
        }

        return targets
    }

    @Synchronized
    fun getAvailableTargets(): List<TrackedTarget> {
        val targets = _availableTargets.toMutableList()
        targets.removeIf {
            val uuid = _activeEditors[it.target.uuid]
            return@removeIf uuid != null && uuid != client.getPlayerId()
        }
        return targets
    }

    fun setActiveTarget(candidate: TrackedTarget) {
        _selectedTarget = candidate
    }

    @Synchronized
    fun releaseTarget() {
        _selectedTarget ?: return
        _selectedTarget = null
        client.sendPacket(ServerboundReleaseGizmoPacket())
    }

    @Synchronized
    fun getActiveEditor(targetId: UUID): UUID? {
        return _activeEditors[targetId]
    }

    @Synchronized
    fun setActiveEditor(targetId: UUID, user: UUID?) {
        if (user != null) {
            _activeEditors[targetId] = user
        } else {
            _activeEditors.remove(targetId)
        }
    }

    @Synchronized
    fun canEditTarget(targetId: UUID): Boolean {
        return _activeEditors[targetId] == null
    }

    @Synchronized
    fun getActiveMode(): EditorMode {
        return _currentMode
    }

    @Synchronized
    fun updateGizmos(
        added: List<GizmoData>,
        removed: List<UUID>,
        updated: List<GizmoData>
    ) {
        val iterator = _availableTargets.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.target.uuid in removed) {
                iterator.remove()
                _activeEditors.remove(entry.target.uuid)
                continue
            }
        }

        _availableTargets.removeIf { it.target.uuid in removed }
        for (newGizmo in added) {
            _availableTargets.add(TrackedTarget(client, fromData(client, newGizmo), _currentMode))
            val editor = newGizmo.editorId
            if (editor != null) setActiveEditor(newGizmo.gizmoId, editor)
        }

        for (existingGizmo in updated) {
            val foundTarget = _availableTargets.find { it.target.uuid == existingGizmo.gizmoId } ?: continue

            val selected = _selectedTarget
            if (selected != null && selected.target.uuid == existingGizmo.gizmoId && client.isDragging()) {
                val position = if (_currentMode == EditorMode.TRANSLATE) null else existingGizmo.position
                val rotation = if (_currentMode == EditorMode.ROTATE) null else existingGizmo.rotation
                val scale = if (_currentMode == EditorMode.SCALE) null else existingGizmo.scale
                foundTarget.target.synchronize(position, rotation, scale)
            } else {
                foundTarget.target.synchronize(existingGizmo.position, existingGizmo.rotation, existingGizmo.scale)
            }
            _activeEditors.remove(existingGizmo.gizmoId)
            val editor = existingGizmo.editorId
            if (editor != null) _activeEditors[existingGizmo.gizmoId] = editor
        }
    }

    companion object {
        private fun fromData(game: IGameClient, target: GizmoData): IEditorTarget {
            return RemoteEditorTarget(
                game,
                target.gizmoId,
                target.position,
                target.rotation,
                target.scale,
                target.allowedModes.map { EditorMode.from(it) }.toSet(),
                target.rotationAxes,
                target.rotationMode
            )
        }
    }

}