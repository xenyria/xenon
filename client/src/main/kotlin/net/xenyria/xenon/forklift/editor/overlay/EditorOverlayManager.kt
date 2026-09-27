package net.xenyria.xenon.forklift.editor.overlay

import net.xenyria.xenon.forklift.editor.IEditorGameClient
import net.xenyria.xenon.forklift.overlay.TextOverlay

/**
 * Manages text overlays for an editor client.
 */
class EditorOverlayManager(val client: IEditorGameClient) {

    private val overlays = ArrayList<TextOverlay>()

    @Synchronized
    fun reset() {
        overlays.clear()
        client.renderOverlays(overlays)
    }

    @Synchronized
    fun updateOverlays(newOverlays: List<TextOverlay>) {
        val newOverlayMap = newOverlays.associateBy { it.id }
        val newList = ArrayList<TextOverlay>(overlays.size)

        for (overlay in newOverlays) {
            val updatedOverlay = newOverlayMap[overlay.id]
            if (updatedOverlay != null) {
                newList.add(updatedOverlay)
            } else {
                newList.add(overlay)
            }
        }

        overlays.clear()
        overlays.addAll(newList)
        client.renderOverlays(overlays)
    }

    @Synchronized
    fun removeOverlays(overlays: Set<String>) {
        this.overlays.removeIf { it.id in overlays }
        client.renderOverlays(this.overlays)
    }


}