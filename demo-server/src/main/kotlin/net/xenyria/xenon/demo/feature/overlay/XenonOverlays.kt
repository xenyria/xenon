package net.xenyria.xenon.demo.feature.overlay

import net.xenyria.xenon.demo.XenonDemoPlugin
import net.xenyria.xenon.demo.player.XenonPlayer
import net.xenyria.xenon.demo.player.XenonPlayerManager
import net.xenyria.xenon.forklift.overlay.OverlayAnchor
import net.xenyria.xenon.forklift.overlay.TextOverlayData
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundRemoveOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundUpdateOverlaysPacket
import org.bukkit.Bukkit

object XenonOverlays {

    private val _overlays = ArrayList<TextOverlayData>()
    private var _elapsedTicks = 0L

    fun startUpdateLoop() {
        Bukkit.getScheduler().runTaskTimer(
            XenonDemoPlugin.instance,
            { _ ->
                _elapsedTicks++
                updateOverlays()
            }, 1L, 1L
        )
    }

    fun updateOverlays() {
        if (_overlays.isEmpty()) return
        _overlays[0].components = "{\"text\": \"Hello World!\"}"
        _overlays[1].components = "{\"text\": \"Elapsed Server Ticks: $_elapsedTicks\"}"
        emitUpdate()
    }

    fun toggle() {
        if (_overlays.isEmpty()) {
            val overlay2 = TextOverlayData("test_overlay2", 1.0, "", OverlayAnchor.TOP_RIGHT, offsetY = 1, offsetX = -1)
            val overlay1 = TextOverlayData("test_overlay1", 1.0, "", OverlayAnchor.TOP_RIGHT, offsetY = 6, offsetX = -1)
            _overlays.add(overlay1)
            _overlays.add(overlay2)

            emitUpdate()
        } else {
            for (player in XenonPlayerManager.activePlayers)
                player.sendXenonMessage(ClientboundRemoveOverlaysPacket(_overlays.map { it.id }))
            _overlays.clear()
        }
    }

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