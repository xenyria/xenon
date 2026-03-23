package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.demo.activity.XenonActivityManager
import net.xenyria.xenon.demo.player.XenonPlayerManager
import org.bukkit.entity.Player

class DiscordActivityCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return
        XenonActivityManager.toggleActivity(XenonPlayerManager.getPlayer(sender.uniqueId))
    }
}