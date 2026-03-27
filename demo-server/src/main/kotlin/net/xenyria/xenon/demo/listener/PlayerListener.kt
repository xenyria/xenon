package net.xenyria.xenon.demo.listener

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent
import net.xenyria.xenon.demo.feature.gizmo.XenonGizmos
import net.xenyria.xenon.demo.player.XenonPlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        XenonPlayerManager.addPlayer(event.player)
    }

    @EventHandler
    fun onPlayerLoaded(event: PlayerClientLoadedWorldEvent) {
        val player = XenonPlayerManager.getPlayerOrNull(event.player.uniqueId) ?: return
        player.startHandshake()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        XenonPlayerManager.removePlayer(event.player)
        XenonGizmos.removeEditor(event.player.uniqueId)
    }

}