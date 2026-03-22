package net.xenyria.xenon

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.KeyMapping.Category
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

object Keybinds {

    val TOGGLE_EDIT_MODE = KeyBindingHelper.registerKeyBinding(
        KeyMapping(
            "key.forklift.general",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F4,
            Category.MULTIPLAYER
        )
    )

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            if (TOGGLE_EDIT_MODE.consumeClick()) {
                if (!xenon.toggleEditMode()) {
                    xenon.logger.warn("Can't enter edit mode: Not connected to a supported server.")
                }
            }
        })
    }
}