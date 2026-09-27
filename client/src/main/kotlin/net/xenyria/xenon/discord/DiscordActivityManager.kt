package net.xenyria.xenon.discord

/**
 * Manages the Discord activity for a client.
 */
class DiscordActivityManager {

    private var api: DiscordAPI? = null
    private var appId: Long? = null
    private var lastActivity: ActivityData? = null

    @Synchronized
    private fun getLastActivity(): ActivityData? {
        return lastActivity
    }

    @Synchronized
    fun updateAppId(appId: Long) {
        this.appId = appId
    }

    @Synchronized
    fun update(data: ActivityData) {
        val appId = appId ?: return
        if (api == null) {
            val api = DiscordAPI(appId)
            api.activitySupplier = { getLastActivity() ?: data }
            api.start()
            this.api = api
        }
        lastActivity = data
    }

    @Synchronized
    fun stop() {
        api?.stop()
        api = null
    }

}