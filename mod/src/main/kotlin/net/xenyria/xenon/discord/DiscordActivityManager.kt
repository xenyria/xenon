package net.xenyria.xenon.discord

object DiscordActivityManager {

    private var _api: DiscordAPI? = null
    private var _appId: Long? = null
    private var _lastActivity: ActivityData? = null

    @Synchronized
    private fun getLastActivity(): ActivityData? {
        return _lastActivity
    }

    @Synchronized
    fun update(data: ActivityData) {
        val appId = _appId ?: return
        if (_api == null) {
            val api = DiscordAPI(appId)
            api.activitySupplier = { getLastActivity() ?: data }
        }
        _lastActivity = data
    }

    @Synchronized
    fun stop() {
        _api?.stop()
        _api = null
    }

}