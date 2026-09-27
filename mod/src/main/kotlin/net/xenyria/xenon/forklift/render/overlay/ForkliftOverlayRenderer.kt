package net.xenyria.xenon.forklift.render.overlay

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier
import net.xenyria.xenon.MOD_ID
import net.xenyria.xenon.Xenon
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.forklift.overlay.OverlayAnchor
import net.xenyria.xenon.forklift.overlay.TextOverlay
import net.xenyria.xenon.game
import net.xenyria.xenon.message.FORKLIFT_COLOR
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import net.xenyria.xenon.util.toComponent
import java.awt.Color

val BACKGROUND_COLOR = Color(32, 32, 32, 196)

/**
 * Class responsible for extracting rendering information about overlays.
 */
class ForkliftOverlayRenderer(private val xenon: Xenon) {

    fun extract(extractor: GuiGraphicsExtractor) {
        val forklift = xenon.getForkliftOrNull()
        if (forklift != null && forklift.editorClient.isActive) {
            extractEditorOverlay(extractor)
        }
        if (XenonClientConfig.config.developer.enableOverlays) {
            visibleOverlays.forEach { TextOverlayRenderer.extract(extractor, it) }
        }
    }

    private fun extractEditorOverlay(graphics: GuiGraphicsExtractor) {
        val width = game.window.guiScaledWidth
        val height = game.window.guiScaledHeight
        val barHeight = 18
        graphics.fill(0, height - barHeight, width, height, BACKGROUND_COLOR.rgb)
        TextOverlayRenderer.extract(
            graphics,
            component = Message(MessageComponent("Forklift", FORKLIFT_COLOR)).toComponent(),
            anchor = OverlayAnchor.BOTTOM_CENTER,
            offsetY = -2
        )
        TextOverlayRenderer.extract(
            graphics,
            component = xenon.forklift.editorClient.getStatusMessage().toComponent(),
            anchor = OverlayAnchor.BOTTOM_LEFT,
            offsetX = 3,
            offsetY = -2
        )
        TextOverlayRenderer.extract(
            graphics,
            component = xenon.forklift.editorClient.getModeMessage().toComponent(),
            anchor = OverlayAnchor.BOTTOM_RIGHT,
            offsetX = -3,
            offsetY = -2
        )
    }


    companion object {
        private var visibleOverlays = ArrayList<TextOverlay>()
        fun updateOverlays(overlays: List<TextOverlay>) {
            this.visibleOverlays = ArrayList(overlays)
        }

        fun initialize(xenon: Xenon) {
            val renderer = ForkliftOverlayRenderer(xenon)
            HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "forklift_overlay")
            ) { extractor, _ -> renderer.extract(extractor) }
        }
    }
}