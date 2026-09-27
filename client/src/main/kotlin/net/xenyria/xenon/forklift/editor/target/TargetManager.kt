package net.xenyria.xenon.forklift.editor.target

import net.xenyria.xenon.core.directionOf
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.gizmo.GizmoData
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundUpdateSelectionPacket
import java.util.*

/**
 * Manages available editor targets & the currently selected target.
 */
class TargetManager(private val client: IEditorGameClient) {

    // Target IDs to Player IDs (this is tracked to prevent multiple players from editing the same entity)
    private val activeEditors = HashMap<UUID, UUID>()
    private val availableTargets = ArrayList<TrackedTarget>()

    // Used for selection logic (which target the player is currently looking at)
    private var selectedGizmoId: UUID? = null

    // Tracks which gizmo was last tracked. Used to prevent sending unnecessary selection update packets to the server
    private var lastSentGizmoId: UUID? = null

    private var selectedTarget: TrackedTarget? = null
    private var currentMode: EditorMode = EditorMode.TRANSLATE

    @Synchronized
    fun updateTarget(target: IEditorTarget) {
        val foundTarget = availableTargets.find { it.target.uuid == target.uuid } ?: return
        foundTarget.target.synchronize(target.position, target.rotation, target.scale)
    }

    @Synchronized
    fun updateTargets(targets: List<IEditorTarget>) {
        val activeIds = availableTargets.map { it.target.uuid }.toSet()
        val newIds = targets.map { it.uuid }

        availableTargets.removeIf { !activeIds.contains(it.target.uuid) }
        for (target in targets) {
            if (!activeIds.contains(target.uuid)) {
                availableTargets.add(TrackedTarget(client, target, currentMode))
            }
        }

        val currentTarget = selectedTarget
        if (currentTarget != null && !newIds.contains(currentTarget.target.uuid)) {
            // Invalidate target if it got removed
            selectedTarget = null
        }
    }

    @Synchronized
    fun selectMode(mode: EditorMode) {
        currentMode = mode
        for (target in availableTargets) target.setMode(mode)
    }

    @Synchronized
    fun updateSelectedGizmo(gizmoId: UUID?) {
        selectedGizmoId = gizmoId
    }

    @Synchronized
    private fun findSelectedGizmo(): TrackedTarget? {
        return findSelectedGizmo(getSortedTargets())
    }

    @Synchronized
    private fun findSelectedGizmo(targets: List<TrackedTarget>): TrackedTarget? {
        val selectedTarget = selectedTarget
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
        if (lastSentGizmoId != newId) {
            lastSentGizmoId = newId
            client.sendPacket(ServerboundUpdateSelectionPacket(newId))
        }
        renderGizmos()
    }

    private fun isInFieldOfView(target: IEditorTarget): Boolean {
        return target == selectedTarget || targetDot(target) >= 0.8
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
            val isSelected = activeId != null && entry.target.uuid == activeId
            if (!isSelected && !isInFieldOfView(entry.target)) continue
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
                    isSelected,
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
        availableTargets.clear()
        releaseTarget()
        selectedGizmoId = null
    }

    @Synchronized
    fun getActiveTarget(): TrackedTarget? {
        return selectedTarget
    }

    @Synchronized
    fun getSortedTargets(): List<TrackedTarget> {
        val targets = getAvailableTargets().toMutableList()
        targets.sortByDescending { targetDot(it.target) }
        val selected = selectedTarget
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
        val targets = availableTargets.toMutableList()
        targets.removeIf {
            val uuid = activeEditors[it.target.uuid]
            return@removeIf uuid != null && uuid != client.getPlayerId()
        }
        return targets
    }

    fun setActiveTarget(candidate: TrackedTarget) {
        selectedTarget = candidate
    }

    @Synchronized
    fun releaseTarget() {
        selectedTarget ?: return
        selectedTarget = null
        client.sendPacket(ServerboundReleaseGizmoPacket())
    }

    @Synchronized
    fun getActiveEditor(targetId: UUID): UUID? {
        return activeEditors[targetId]
    }

    @Synchronized
    fun setActiveEditor(targetId: UUID, user: UUID?) {
        if (user != null) {
            activeEditors[targetId] = user
        } else {
            activeEditors.remove(targetId)
        }
    }

    @Synchronized
    fun canEditTarget(targetId: UUID): Boolean {
        return activeEditors[targetId] == null
    }

    @Synchronized
    fun getActiveMode(): EditorMode {
        return currentMode
    }

    @Synchronized
    fun updateGizmos(
        added: List<GizmoData>,
        removed: List<UUID>,
        updated: List<GizmoData>
    ) {
        val iterator = availableTargets.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.target.uuid in removed) {
                iterator.remove()
                activeEditors.remove(entry.target.uuid)
                continue
            }
        }

        availableTargets.removeIf { it.target.uuid in removed }
        for (newGizmo in added) {
            availableTargets.add(TrackedTarget(client, fromData(client, newGizmo), currentMode))
            val editor = newGizmo.editorId
            if (editor != null) setActiveEditor(newGizmo.gizmoId, editor)
        }

        for (existingGizmo in updated) {
            val foundTarget = availableTargets.find { it.target.uuid == existingGizmo.gizmoId } ?: continue

            val selected = selectedTarget
            if (selected != null && selected.target.uuid == existingGizmo.gizmoId && client.isDragging()) {
                val position = if (currentMode == EditorMode.TRANSLATE) null else existingGizmo.position
                val rotation = if (currentMode == EditorMode.ROTATE) null else existingGizmo.rotation
                val scale = if (currentMode == EditorMode.SCALE) null else existingGizmo.scale
                foundTarget.target.synchronize(position, rotation, scale)
            } else {
                foundTarget.target.synchronize(existingGizmo.position, existingGizmo.rotation, existingGizmo.scale)
            }
            activeEditors.remove(existingGizmo.gizmoId)
            val editor = existingGizmo.editorId
            if (editor != null) activeEditors[existingGizmo.gizmoId] = editor
        }
    }

    companion object {
        /**
         * Creates a remote editor target from a [GizmoData] object.
         */
        private fun fromData(game: IEditorGameClient, target: GizmoData): IEditorTarget {
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