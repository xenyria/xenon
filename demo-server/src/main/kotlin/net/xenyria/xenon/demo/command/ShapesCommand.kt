package net.xenyria.xenon.demo.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.xenyria.xenon.core.calculateDirection
import net.xenyria.xenon.demo.feature.shape.XenonShapes
import net.xenyria.xenon.shape.impl.*
import org.bukkit.entity.Player
import org.joml.Vector3d
import java.awt.Color
import java.util.*

class ShapesCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) return

        val origin = Vector3d(sender.location.x, sender.location.y, sender.location.z)

        val pyramidCenter = Vector3d(origin).add(Vector3d(2.5, 1.0, 0.5))
        val pyramidApex = Vector3d(pyramidCenter).add(Vector3d(0.0, -1.0, 0.0))

        val polygonCenter = Vector3d(origin).add(Vector3d(4.5, 0.5, 0.5))
        val polygonPoints = mutableListOf<Vector3d>()
        for (rotation in listOf(0F, 45F, 90F, 180F, 220F, 320F, 340F)) {
            polygonPoints.add(
                Vector3d(polygonCenter).add(
                    calculateDirection(rotation, 0.0F)
                ).add(0.0, rotation / 200.0, 0.0)
            )
        }

        val pathStart = Vector3d(origin).add(Vector3d(6.5, 0.5, 0.5))
        val sphereStart = Vector3d(origin).add(Vector3d(8.5, 0.5, 0.5))

        XenonShapes.addAll(
            listOf(
                BoxShape(
                    "box_" + UUID.randomUUID(),
                    origin,
                    BoxShapeProperties(
                        dimensions = Vector3d(1.0),
                        centerTextVertically = false,
                        visibleThroughWalls = true,
                    ),
                    textLines = listOf(
                        "{\"text\": \"Box\", \"color\":\"aqua\"}"
                    )
                ),
                PyramidShape(
                    "pyramid_" + UUID.randomUUID(),
                    pyramidApex,
                    PyramidShapeProperties(pyramidCenter, baseSize = 1.0F),
                    textLines = listOf(
                        "{\"text\": \"Pyramid\", \"color\":\"green\"}",
                    )
                ),
                PolygonShape(
                    "polygon_" + UUID.randomUUID(),
                    pyramidApex,
                    PolygonShapeProperties(Color.WHITE, polygonPoints),
                    textLines = listOf(
                        "{\"text\": \"Polygon\", \"color\":\"light_purple\"}"
                    )
                ),
                PathShape(
                    "path_" + UUID.randomUUID(),
                    pathStart,
                    PathShapeProperties(
                        Color.WHITE, isOpen = true, isSmooth = true, visibleThroughWalls = false,
                        listOf(
                            pathStart,
                            Vector3d(pathStart).add(0.0, 0.0, 1.0),
                            Vector3d(pathStart).add(1.0, 0.0, 2.0),
                            Vector3d(pathStart).add(0.0, 0.0, 3.0),
                            Vector3d(pathStart).add(1.0, 0.0, 4.0),
                            Vector3d(pathStart).add(-1.0, 0.0, 5.0),
                            Vector3d(pathStart).add(0.0, 0.0, 6.0),
                        )
                    ),
                    textLines = listOf(
                        "{\"text\": \"Path\", \"color\":\"yellow\"}"
                    )
                ),
                SphereShape(
                    "sphere_" + UUID.randomUUID(),
                    sphereStart,
                    SphereShapeProperties(1.0F, color = Color.WHITE, visibleThroughWalls = false),
                    textLines = listOf(
                        "{\"text\": \"Sphere\", \"color\":\"blue\"}"
                    )
                ),
            )
        )
    }
}