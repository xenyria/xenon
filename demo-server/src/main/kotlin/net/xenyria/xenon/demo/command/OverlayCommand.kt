package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.demo.feature.overlay.XenonOverlays
import org.bukkit.entity.Player

class OverlayCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return

        XenonOverlays.toggle()
    }
}