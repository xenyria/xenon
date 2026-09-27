package net.xenyria.xenon.forklift.render

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import org.joml.Math.toRadians
import org.joml.Quaternionf
import org.joml.Vector3dc
import java.awt.Color

private const val DEFAULT_TEXT_SCALE = 1.0F / 48.0F

/**
 * Helper class for rendering text in the world.
 */
class WorldTextRenderer(val context: LevelRenderContext) {

    private val camera: Vec3 = context.levelState().cameraRenderState.pos

    fun renderCentered(
        position: Vector3dc,
        lines: List<Component>,
        seeThrough: Boolean,
        scale: Float = DEFAULT_TEXT_SCALE
    ) {
        val font = Minecraft.getInstance().font

        val lineSpacing = 2
        val totalHeight = (font.lineHeight * lines.size) + (lineSpacing * lines.size)
        var currentHeight = -totalHeight / 2.0

        for (line in lines) {
            val width = font.width(line)

            context.poseStack().pushPose()
            context.poseStack().translate(-camera.x, -camera.y, -camera.z)
            context.poseStack().translate(position.x(), position.y(), position.z())

            // Rotate the text to always face the camera
            val quat = Quaternionf()
            quat.rotateLocalX(toRadians(-context.gameRenderer().mainCamera().xRot()))
            quat.rotateLocalY(toRadians(180 - context.gameRenderer().mainCamera().yRot()))
            context.poseStack().last().rotate(quat)

            context.poseStack().scale(scale, -scale, scale)
            context.poseStack().translate(-(width / 2.0), 0.0, 0.0)
            context.poseStack().translate(0.0, currentHeight, 0.0)

            context.submitNodeCollector().submitText(
                context.poseStack(),
                0.0F, 0.0F, line.visualOrderText,
                true,
                if (seeThrough) Font.DisplayMode.SEE_THROUGH else Font.DisplayMode.NORMAL,
                Color.WHITE.rgb, // Light coords
                Color.WHITE.rgb, // Color
                0, // Background color
                0 // Outline color
            )
            context.poseStack().popPose()
            currentHeight += (lineSpacing + font.lineHeight)
        }
    }

}