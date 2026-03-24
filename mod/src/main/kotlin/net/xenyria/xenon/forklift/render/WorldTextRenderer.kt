package net.xenyria.xenon.forklift.render

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import org.joml.Math.toRadians
import org.joml.Quaternionf
import org.joml.Vector3dc
import java.awt.Color

private const val DEFAULT_TEXT_SCALE = 1.0F / 48.0F

class WorldTextRenderer(val context: WorldRenderContext) {

    private val camera: Vec3 = context.worldState().cameraRenderState.pos

    fun renderCentered(position: Vector3dc, lines: List<Component>, seeThrough: Boolean, scale: Float = DEFAULT_TEXT_SCALE) {
        val font = Minecraft.getInstance().font

        val lineSpacing = 2
        val totalHeight = (font.lineHeight * lines.size) + (lineSpacing * lines.size)
        var currentHeight = -totalHeight / 2.0

        for (line in lines) {
            val width = font.width(line)

            context.matrices().pushPose()
            context.matrices().translate(-camera.x, -camera.y, -camera.z)
            context.matrices().translate(position.x(), position.y(), position.z())

            // Rotate the text to always face the camera
            val quat = Quaternionf()
            quat.rotateLocalX(toRadians(-context.gameRenderer().mainCamera.xRot()))
            quat.rotateLocalY(toRadians(180 - context.gameRenderer().mainCamera.yRot()))
            context.matrices().last().rotate(quat)

            context.matrices().scale(scale, -scale, scale)
            context.matrices().translate(-(width / 2.0), 0.0, 0.0)
            context.matrices().translate(0.0, currentHeight, 0.0)

            context.commandQueue().submitText(
                context.matrices(),
                1.0F, 1.0F, line.visualOrderText,
                true,
                if (seeThrough) Font.DisplayMode.SEE_THROUGH else Font.DisplayMode.NORMAL,
                Color.WHITE.rgb, // Light coords
                Color.WHITE.rgb, // Color
                0, // Background color
                0 // Outline color
            )
            context.matrices().popPose()
            currentHeight += (lineSpacing + font.lineHeight)
        }
    }

}