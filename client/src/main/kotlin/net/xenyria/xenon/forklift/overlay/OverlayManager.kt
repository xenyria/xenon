package net.xenyria.xenon.forklift.overlay

class OverlayManager {

    private val _textOverlays = ArrayList<TextOverlayData>()

    @Synchronized
    fun findOverlay(id: String): TextOverlayData? {
        return _textOverlays.find { it.id == id }
    }

    @Synchronized
    fun addOverlay(textOverlay: TextOverlayData) {
        _textOverlays.add(textOverlay)
    }

    @Synchronized
    fun getOrCreateTextOverlay(id: String) {
        var overlay = findOverlay(id)
        if (overlay == null) {
            overlay = TextOverlayData(id)
        }
    }

}