package net.xenyria.xenon.demo.player

import net.xenyria.xenon.demo.XenonDemoPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object XenonPlayerManager {

    val activePlayers: List<XenonPlayer>
        get() = _players.filter { it.isActive }

    private val _players = ArrayList<XenonPlayer>()

    fun startUpdateLoop() {
        Bukkit.getScheduler().runTaskTimer(
            XenonDemoPlugin.instance,
            { _ -> updatePlayers() }, 1L, 1L
        )
    }

    private fun updatePlayers() {
        _players.forEach { it.update() }
    }

    fun addPlayer(player: Player) {
        _players.add(XenonPlayer(player))
    }

    fun getPlayerOrNull(uuid: UUID): XenonPlayer? {
        return _players.find { it.player.uniqueId == uuid }
    }

    fun getPlayer(uuid: UUID): XenonPlayer {
        return requireNotNull(getPlayerOrNull(uuid)) { "Couldn't find any player by ID $uuid" }
    }

    fun removePlayer(player: Player) {
        _players.removeIf { it.player.uniqueId == player.uniqueId }
    }

}