package net.xenyria

import net.fabricmc.api.ModInitializer
import net.xenyria.xenon.discord.ActivityData
import org.slf4j.LoggerFactory

object Xenon : ModInitializer {
    private val logger = LoggerFactory.getLogger("xenon")

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        val discordAPI = DiscordAPI(1410669620297469952)
        discordAPI.start()
        discordAPI.activitySupplier = {
            ActivityData(
                details = ":3",
                state = "meow"
            )
        }
    }
}