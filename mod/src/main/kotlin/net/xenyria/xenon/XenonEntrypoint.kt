package net.xenyria.xenon

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

object XenonEntrypoint : ModInitializer {

    private fun getVersion(): String {
        // Get the current mod version
        val modList = FabricLoader.getInstance().allMods
        for (mod in modList) {
            if (mod.metadata.name.equals("xenon", ignoreCase = true))
                return mod.metadata.version.friendlyString
        }
        throw IllegalStateException("Unable to retrieve Xenon version from mod list.")
    }

    override fun onInitialize() {
        Xenon.create(getVersion())
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        //val discordAPI = DiscordAPI(1410669620297469952)
        //discordAPI.start()
        //discordAPI.activitySupplier = {
        //    ActivityData(
        //        details = ":3",
        //        state = "meow"
        //    )
        //}
    }
}