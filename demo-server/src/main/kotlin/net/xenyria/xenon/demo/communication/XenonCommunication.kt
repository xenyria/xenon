package net.xenyria.xenon.demo.communication

import net.xenyria.xenon.CHANNEL_ID
import net.xenyria.xenon.demo.XenonDemoPlugin
import net.xenyria.xenon.demo.player.XenonPlayerManager
import net.xenyria.xenon.protocol.parsePacket
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream

object XenonCommunication : PluginMessageListener {
    fun initialize() {
        Bukkit.getMessenger().registerIncomingPluginChannel(XenonDemoPlugin.instance, CHANNEL_ID, this)
        Bukkit.getMessenger().registerOutgoingPluginChannel(XenonDemoPlugin.instance, CHANNEL_ID)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, data: ByteArray) {
        val player = XenonPlayerManager.getPlayer(player.uniqueId)
        // Parse packet via Xenon
        val packet = parsePacket(ByteArrayInputStream(data))
        player.onMessage(packet)
    }


}