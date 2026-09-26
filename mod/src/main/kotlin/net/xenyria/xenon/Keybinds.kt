package net.xenyria.xenon

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.KeyMapping.Category
import net.minecraft.client.Minecraft

object Keybinds {

    val TOGGLE_EDIT_MODE = KeyMappingHelper.registerKeyMapping(
        KeyMapping(
            "forklift_key_edit_mode",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_F4,
            Category.MULTIPLAYER
        )
    )

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { _: Minecraft ->
            if (TOGGLE_EDIT_MODE.consumeClick()) {
                if (!xenon.toggleEditMode()) {
                    LOGGER.warn("Can't enter edit mode: Not connected to a supported server.")
                }
            }
        })
    }
}