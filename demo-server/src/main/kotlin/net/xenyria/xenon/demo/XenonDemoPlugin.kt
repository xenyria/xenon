package net.xenyria.xenon.demo

import net.xenyria.xenon.demo.activity.XenonActivityManager
import net.xenyria.xenon.demo.command.DiscordActivityCommand
import net.xenyria.xenon.demo.command.GizmoCommand
import net.xenyria.xenon.demo.communication.XenonCommunication
import net.xenyria.xenon.demo.listener.PlayerListener
import net.xenyria.xenon.demo.player.XenonPlayerManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class XenonDemoPlugin : JavaPlugin() {

    override fun onEnable() {
        super.onEnable()
        _instance = this
        XenonPlayerManager.startUpdateLoop()
        XenonCommunication.initialize()
        XenonActivityManager.initialize()
        registerCommand("gizmo", GizmoCommand())
        registerCommand("discord_activity", DiscordActivityCommand())
        Bukkit.getPluginManager().registerEvents(PlayerListener, this)
    }

    companion object {
        private var _instance: XenonDemoPlugin? = null
        val instance: XenonDemoPlugin
            get() = requireNotNull(_instance)
    }

}