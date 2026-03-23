package net.xenyria.xenon

import net.xenyria.xenon.config.RichPresenceMode
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.forklift.Forklift
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.util.getCurrentServer

class Session(client: IGameClient, editModeAvailable: Boolean) {

    val isTrusted: Boolean = TrustedServers.isTrusted(getCurrentServer())
    val forklift: Forklift? = if (editModeAvailable) Forklift(client) else null

    fun canApplyActivity(): Boolean {
        val mode = XenonClientConfig.config.misc.discordActivityMode
        if (mode == RichPresenceMode.NONE) return false
        if (mode == RichPresenceMode.TRUSTED_ONLY) return isTrusted
        return true
    }


}