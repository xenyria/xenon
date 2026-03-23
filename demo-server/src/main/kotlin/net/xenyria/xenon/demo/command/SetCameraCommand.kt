package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.demo.player.XenonPlayerManager
import org.bukkit.entity.Player

class SetCameraCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return
        if (args.size != 1) {
            sender.sendMessage("/set_camera <perspective>")
            return
        }

        val perspective = CameraPerspective.valueOf(args[0].uppercase())
        val player = XenonPlayerManager.getPlayer(sender.uniqueId)
        player.setCamera(perspective)
    }
}