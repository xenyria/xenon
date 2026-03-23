package net.xenyria.xenon.config

import net.xenyria.xenon.discord.DiscordActivityManager
import net.xenyria.xenon.xenon

object XenonClientConfig {

    private var _config = XenonConfig()

    @get:Synchronized
    val config: XenonConfig get() = _config.copy()

    @Synchronized
    fun setDiscordRichPresence(enabled: RichPresenceMode) {
        _config.misc.discordActivityMode = enabled

        val session = xenon.getSessionOrNull() ?: return
        if (!session.canApplyActivity()) DiscordActivityManager.stop()
    }

    @Synchronized
    fun mutateConfig(block: XenonConfig.() -> Unit) {
        val copy = config
        block(copy)
        _config = copy
    }

}