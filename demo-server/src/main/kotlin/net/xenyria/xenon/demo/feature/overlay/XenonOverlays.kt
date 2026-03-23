package net.xenyria.xenon.demo.feature.overlay

import net.xenyria.xenon.demo.player.XenonPlayer
import net.xenyria.xenon.demo.player.XenonPlayerManager
import net.xenyria.xenon.forklift.overlay.TextOverlayData
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundRemoveOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundUpdateOverlaysPacket

object XenonOverlays {

    private val _overlays = ArrayList<TextOverlayData>()

    fun spawnAll(player: XenonPlayer) {
        player.sendXenonMessage(ClientboundUpdateOverlaysPacket(_overlays))
    }

    fun add(overlay: TextOverlayData) {
        _overlays.add(overlay)
        XenonPlayerManager.activePlayers.forEach {
            it.sendXenonMessage(ClientboundUpdateOverlaysPacket(listOf(overlay)))
        }
    }

    fun emitUpdate() {
        XenonPlayerManager.activePlayers.forEach {
            it.sendXenonMessage(ClientboundUpdateOverlaysPacket(_overlays))
        }
    }

    fun remove(overlay: TextOverlayData) {
        _overlays.remove(overlay)
        XenonPlayerManager.activePlayers.forEach {
            it.sendXenonMessage(ClientboundRemoveOverlaysPacket(listOf(overlay.id)))
        }
    }
}