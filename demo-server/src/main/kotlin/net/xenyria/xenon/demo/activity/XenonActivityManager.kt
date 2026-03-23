package net.xenyria.xenon.demo.activity

import net.xenyria.xenon.demo.XenonDemoPlugin
import net.xenyria.xenon.demo.player.XenonPlayer
import net.xenyria.xenon.demo.player.XenonPlayerManager
import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.protocol.clientbound.misc.ClientboundUpdateActivityAppPacket
import net.xenyria.xenon.protocol.clientbound.misc.ClientboundUpdateActivityPacket
import org.bukkit.Bukkit
import java.util.*

object XenonActivityManager {

    val appId: Long
        get() {
            return System.getenv("XENON_ACTIVITY_APP_ID")?.toLongOrNull() ?: 0L
        }

    private val _subscribers = HashSet<UUID>()
    private var _elapsedSeconds = 0L

    fun initialize() {
        Bukkit.getScheduler().runTaskTimer(XenonDemoPlugin.instance, { _ ->
            val activity = ActivityData(
                state = "Discord Activity Test",
                details = "Hello from Xenon! (elapsed seconds: $_elapsedSeconds)",
                start = 1774281127096
            )
            for (player in XenonPlayerManager.activePlayers) {
                player.sendXenonMessage(ClientboundUpdateActivityPacket(activity))
            }
            _elapsedSeconds++
        }, 20L, 20L)
    }

    fun toggleActivity(client: XenonPlayer) {
        val uuid = client.player.uniqueId
        if (_subscribers.contains(uuid)) {
            _subscribers.remove(uuid)
            client.sendMessage("Discord Activity disabled.")
        } else {
            _subscribers.add(uuid)
            client.sendMessage("Discord Activity enabled.")
            client.sendXenonMessage(ClientboundUpdateActivityAppPacket(appId))
        }
    }


}