package net.xenyria.xenon.forklift.editor.overlay

import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.forklift.overlay.TextOverlayData

class EditorOverlayManager(val client: IGameClient) {

    private val _overlays = ArrayList<TextOverlayData>()

    @Synchronized
    fun reset() {
        _overlays.clear()
        client.renderOverlays(_overlays)
    }

    @Synchronized
    fun updateOverlays(newOverlays: List<TextOverlayData>) {
        val newOverlayMap = newOverlays.associateBy { it.id }
        val newList = ArrayList<TextOverlayData>(_overlays.size)

        for (overlay in _overlays) {
            val updatedOverlay = newOverlayMap[overlay.id]
            if (updatedOverlay != null) {
                newList.add(updatedOverlay)
            } else {
                newList.add(overlay)
            }
        }

        _overlays.clear()
        _overlays.addAll(newList)
        client.renderOverlays(_overlays)
    }

    @Synchronized
    fun removeOverlays(overlays: Set<String>) {
        _overlays.removeIf { it.id in overlays }
    }


}