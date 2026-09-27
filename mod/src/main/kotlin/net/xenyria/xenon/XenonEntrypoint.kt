package net.xenyria.xenon

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

/**
 * Main entry point for Xenon.
 */
object XenonEntrypoint : ModInitializer {

    /**
     * Helper function for obtaining the current mod version.
     */
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
    }
}