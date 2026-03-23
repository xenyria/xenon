package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.demo.player.XenonPlayerManager
import org.bukkit.entity.Player

class LockCameraCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return
        if (args.size !in 1..2) {
            sender.sendMessage("/lock_camera <boolean> <perspective>")
            return
        }

        val isLocked = args[0].toBoolean()
        val perspective = if (args.size == 2) CameraPerspective.valueOf(args[1].uppercase()) else null
        val player = XenonPlayerManager.getPlayer(sender.uniqueId)
        player.setCameraLock(isLocked, perspective)
    }
}