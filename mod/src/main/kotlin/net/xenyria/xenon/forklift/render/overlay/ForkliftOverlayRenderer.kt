package net.xenyria.xenon.forklift.render.overlay

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.Identifier
import net.xenyria.xenon.MOD_ID
import net.xenyria.xenon.Xenon
import net.xenyria.xenon.forklift.overlay.OverlayAnchor
import net.xenyria.xenon.game
import net.xenyria.xenon.message.FORKLIFT_COLOR
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import net.xenyria.xenon.util.toComponent
import java.awt.Color

class ForkliftOverlayRenderer(private val xenon: Xenon) {

    fun render(graphics: GuiGraphics, tickCounter: DeltaTracker) {
        val forklift = xenon.getForkliftOrNull()
        if (forklift != null && forklift.editor.isActive) {
            renderEditorOverlay(graphics)
        }
        for (anchor in OverlayAnchor.entries) {
            //val textOverlay = TextOverlayData(
            //    "test",
            //    components = "{\"text\": \"$anchor\"}",
            //    anchor = anchor
            //)
            //TextOverlayRenderer.render(graphics, textOverlay)
        }
    }

    private fun renderEditorOverlay(graphics: GuiGraphics) {
        val bg = Color(32, 32, 32, 196)
        val width = game.window.guiScaledWidth
        val height = game.window.guiScaledHeight
        val barHeight = 18
        graphics.fill(0, height - barHeight, width, height, bg.rgb)
        TextOverlayRenderer.render(
            graphics,
            component = Message(
                MessageComponent("Forklift", FORKLIFT_COLOR)
            ).toComponent(),
            anchor = OverlayAnchor.BOTTOM_CENTER,
            offsetY = -2
        )
        TextOverlayRenderer.render(
            graphics,
            component = xenon.forklift.editor.getStatusMessage().toComponent(),
            anchor = OverlayAnchor.BOTTOM_LEFT,
            offsetX = 3,
            offsetY = -2
        )
        TextOverlayRenderer.render(
            graphics,
            component = xenon.forklift.editor.getModeMessage().toComponent(),
            anchor = OverlayAnchor.BOTTOM_RIGHT,
            offsetX = -3,
            offsetY = -2
        )
    }


    companion object {
        fun initialize(xenon: Xenon) {
            val renderer = ForkliftOverlayRenderer(xenon)
            HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "forklift_overlay"),
                renderer::render
            )
        }
    }
}