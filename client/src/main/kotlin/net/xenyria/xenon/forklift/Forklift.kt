package net.xenyria.xenon.forklift

import net.xenyria.xenon.forklift.editor.Editor
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.editor.RenderableGizmo

/**
 * Main class for the map editor features provided by Xenon (formerly known as Forklift)
 */
class Forklift(val client: IGameClient) {

    val editor = Editor(client)

    fun onTick() {
        editor.onTick()
        renderGizmos()
    }

    fun renderGizmos() {
        val camera = client.getCamera()
        val targets = editor.targetManager.getAvailableTargets().toMutableList()
        targets.sortBy { it.target.position.distance(camera.position) }

        val activeId = editor.targetManager.getActiveTarget()?.target?.uuid

        val renderList = ArrayList<RenderableGizmo>()
        var index = 0
        for (target in targets) {
            if (!target.isInFieldOfView()) continue

            val editorPlayer = editor.targetManager.getActiveEditor(target.target.uuid)

            if (editorPlayer != null && editorPlayer != client.getPlayerId()) continue

            renderList.add(RenderableGizmo(target, activeId != null && target.target.uuid == activeId, index++))
        }
        client.renderGizmos(renderList)
    }

    fun reset() {
        editor.reset()
    }

}