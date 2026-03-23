package net.xenyria.xenon.discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import java.time.Instant

class DiscordAPI {

    private val _clientId: Long
    private var _thread: Thread? = null
    private var _running: Boolean = false
    private var _lastActivity: ActivityData? = null
    var activitySupplier: (() -> ActivityData)? = null

    constructor(clientId: Long) {
        this._clientId = clientId
    }

    @Synchronized
    fun start() {
        _running = false
        _thread?.join()
        _running = true
        val newThread = Thread({
            CreateParams().use {
                it.clientID = _clientId
                it.flags = CreateParams.getDefaultFlags()
                Core(it).use { core ->
                    while (_running) {
                        val activity = activitySupplier?.invoke()
                        if (activity == null) {
                            core.activityManager().clearActivity()
                        } else {
                            applyActivity(core, activity)
                        }
                        Thread.sleep(1000)
                        core.runCallbacks()
                    }
                    core.activityManager().clearActivity()
                }
            }
        }, "Xenon Discord Thread")
        _thread = newThread
        newThread.start()
    }

    private fun padMinLength(input: String, minLength: Int): String {
        var text = input
        while (text.length < minLength) {
            text = text.padStart(minLength, ' ')
        }
        return text
    }

    private fun applyActivity(core: Core, activityData: ActivityData?) {
        if (activityData == null) {
            core.activityManager().clearActivity()
            return
        }
        if (activityData == _lastActivity) return

        val activity = Activity()
        if (activityData.state != null)
            activity.state = padMinLength(activityData.state!!, 2)
        if (activityData.details != null)
            activity.details = padMinLength(activityData.details!!, 2)

        if (activityData.start != null) {
            activity.timestamps().start = Instant.ofEpochMilli(activityData.start!!)
        }
        activity.type = ActivityType.PLAYING
        _lastActivity = activityData
        core.activityManager().updateActivity(activity)
    }

    @Synchronized
    fun stop() {
        _running = false
        _thread?.interrupt()
        _thread?.join()
        _thread = null
    }

}