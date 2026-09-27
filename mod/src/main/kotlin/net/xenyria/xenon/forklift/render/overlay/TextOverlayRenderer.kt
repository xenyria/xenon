package net.xenyria.xenon.forklift.render.overlay

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ActiveTextCollector
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.TextAlignment
import net.minecraft.network.chat.Component
import net.xenyria.xenon.forklift.overlay.OverlayAnchor
import net.xenyria.xenon.forklift.overlay.TextOverlay
import net.xenyria.xenon.game
import net.xenyria.xenon.util.parseComponentFromJSON
import org.joml.Matrix3x2f

/**
 * Helper class for rendering text in the HUD.
 */
object TextOverlayRenderer {

    data class RenderOrigin(val alignment: TextAlignment, val x: Int, val y: Int)

    private fun determineRenderOrigin(anchor: OverlayAnchor): RenderOrigin {
        val height = Minecraft.getInstance().font.lineHeight
        when (anchor) {
            OverlayAnchor.TOP_LEFT -> return RenderOrigin(
                TextAlignment.LEFT, 0, 0
            )

            OverlayAnchor.TOP_CENTER -> return RenderOrigin(
                TextAlignment.CENTER, (game.window.guiScaledWidth / 2.0).toInt(), 0
            )

            OverlayAnchor.TOP_RIGHT -> return RenderOrigin(
                TextAlignment.RIGHT, game.window.guiScaledWidth, 0
            )

            OverlayAnchor.CENTER_LEFT -> return RenderOrigin(
                TextAlignment.LEFT, 0, (game.window.guiScaledHeight / 2.0).toInt() - (height / 2)
            )

            OverlayAnchor.CENTER_CENTER -> return RenderOrigin(
                TextAlignment.CENTER,
                (game.window.guiScaledWidth / 2.0).toInt(),
                (game.window.guiScaledHeight / 2.0).toInt() - (height / 2)
            )

            OverlayAnchor.CENTER_RIGHT -> return RenderOrigin(
                TextAlignment.RIGHT,
                game.window.guiScaledWidth,
                (game.window.guiScaledHeight / 2.0).toInt() - (height / 2)
            )

            OverlayAnchor.BOTTOM_LEFT -> return RenderOrigin(
                TextAlignment.LEFT, 0, game.window.guiScaledHeight - height
            )

            OverlayAnchor.BOTTOM_CENTER -> {
                return RenderOrigin(
                    TextAlignment.CENTER,
                    (game.window.guiScaledWidth / 2.0).toInt(),
                    game.window.guiScaledHeight - height
                )
            }

            OverlayAnchor.BOTTOM_RIGHT -> {
                return RenderOrigin(
                    TextAlignment.RIGHT,
                    game.window.guiScaledWidth,
                    game.window.guiScaledHeight - height
                )
            }
        }
    }


    fun extract(graphics: GuiGraphicsExtractor, overlay: TextOverlay) {
        extract(
            graphics,
            overlay.opacity,
            parseComponentFromJSON(overlay.components),
            overlay.anchor,
            overlay.offsetX,
            overlay.offsetY
        )
    }

    fun extract(
        graphics: GuiGraphicsExtractor, opacity: Double = 1.0,
        component: Component,
        anchor: OverlayAnchor = OverlayAnchor.TOP_LEFT,
        offsetX: Int = 0, offsetY: Int = 0
    ) {
        // Discard any text that is below 10% opacity (it'd be discarded by the fragment shader anyway)
        if (opacity < 0.1) return

        val origin = determineRenderOrigin(anchor)
        val x = origin.x + offsetX
        val y = origin.y + offsetY

        graphics.textRenderer().accept(
            origin.alignment, x + offsetX, y + offsetY,
            ActiveTextCollector.Parameters(
                Matrix3x2f(graphics.pose()),
                opacity.toFloat().coerceIn(0.0F, 1.0F),
                null
            ), component
        )
    }
}