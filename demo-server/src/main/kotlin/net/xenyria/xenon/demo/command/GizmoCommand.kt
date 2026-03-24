package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.RotationMode
import net.xenyria.xenon.demo.applyRotation
import net.xenyria.xenon.demo.feature.gizmo.EditorEntity
import net.xenyria.xenon.demo.feature.gizmo.XenonGizmos
import net.xenyria.xenon.forklift.TransformationMode
import org.bukkit.block.BlockType
import org.bukkit.craftbukkit.block.data.CraftBlockData
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.joml.Vector3d

class GizmoCommand : BasicCommand {
    private fun sendUsage(player: Player) {
        player.sendMessage("/gizmo <euler|yaw_pitch>")
    }

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return

        if (args.size != 1) {
            sendUsage(sender)
            return
        }

        val spawnLoc = sender.location
        spawnLoc.yaw = 0.0F
        spawnLoc.pitch = 0.0F

        if (args[0] == "euler") {

            val display = sender.world.spawnEntity(spawnLoc, EntityType.BLOCK_DISPLAY) as BlockDisplay
            display.transformation.scale
            display.block = CraftBlockData.newData(BlockType.OAK_PLANKS, "")
            display.interpolationDuration = 2
            display.teleportDuration = 2

            val rotation = Vector3d(0.0)
            val entity = EditorEntity(
                { display.remove() },
                positionGetter = {
                    val loc = display.location
                    Vector3d(loc.x, loc.y, loc.z)
                },
                positionSetter = { newPos ->
                    val newLoc = display.location
                    newLoc.set(newPos.x(), newPos.y(), newPos.z())
                    display.teleport(newLoc)
                    println(newLoc)
                },
                rotationGetter = { Vector3d(rotation) },
                rotationSetter = { newRot ->
                    rotation.set(newRot)
                    applyRotation(display, rotation)
                },
                scaleGetter = {
                    val transform = display.transformation
                    Vector3d(transform.scale.x.toDouble(), transform.scale.y.toDouble(), transform.scale.z.toDouble())
                },
                scaleSetter = { scale ->
                    val transform = display.transformation
                    transform.scale.set(scale)
                    display.transformation = transform
                },
                allowedModes = setOf(TransformationMode.TRANSLATE, TransformationMode.SCALE, TransformationMode.ROTATE),
                rotationAxes = setOf(Axis.X, Axis.Y, Axis.Z),
                rotationMode = RotationMode.EULER
            )
            XenonGizmos.addEntity(entity)
        } else if (args[0] == "yaw_pitch") {
            val display = sender.world.spawnEntity(spawnLoc, EntityType.BLOCK_DISPLAY) as BlockDisplay
            display.transformation.scale
            display.block = CraftBlockData.newData(BlockType.OAK_PLANKS, "")
            display.teleportDuration = 2

            val entity = EditorEntity(
                { display.remove() },
                positionGetter = {
                    val loc = display.location
                    Vector3d(loc.x, loc.y, loc.z)
                },
                positionSetter = { newPos ->
                    val newLoc = display.location
                    newLoc.set(newPos.x(), newPos.y(), newPos.z())
                    display.teleport(newLoc)
                    println(newLoc)
                },
                rotationGetter = { Vector3d(display.pitch.toDouble(), display.yaw.toDouble(), 0.0) },
                rotationSetter = { newRot ->
                    val newPos = display.location
                    newPos.pitch = newRot.x().toFloat()
                    newPos.yaw = newRot.y().toFloat()
                    display.teleport(newPos)
                },
                scaleGetter = {
                    val transform = display.transformation
                    Vector3d(transform.scale.x.toDouble(), transform.scale.y.toDouble(), transform.scale.z.toDouble())
                },
                scaleSetter = { scale ->
                    val transform = display.transformation
                    transform.scale.set(scale)
                    display.transformation = transform
                },
                allowedModes = setOf(TransformationMode.TRANSLATE, TransformationMode.SCALE, TransformationMode.ROTATE),
                rotationAxes = setOf(Axis.X, Axis.Y, Axis.Z),
                rotationMode = RotationMode.YAW_PITCH
            )
            XenonGizmos.addEntity(entity)
        } else {
            sendUsage(sender)
        }
    }
}