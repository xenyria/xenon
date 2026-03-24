package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.core.Axis
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
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return

        val spawnLoc = sender.location
        spawnLoc.yaw = 0.0F
        spawnLoc.pitch = 0.0F

        val display = sender.world.spawnEntity(spawnLoc, EntityType.BLOCK_DISPLAY) as BlockDisplay
        display.transformation.scale
        display.block = CraftBlockData.newData(BlockType.OAK_PLANKS, "")
        display.interpolationDuration = 2

        var rotation = Vector3d(0.0)
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
            allowedModes = setOf(TransformationMode.TRANSLATE, TransformationMode.SCALE),
            rotationAxes = setOf(Axis.X, Axis.Y, Axis.Z)
        )
        XenonGizmos.addEntity(entity)
    }
}