package net.xenyria

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import net.xenyria.xenon.discord.ActivityData
import java.time.Instant
import java.time.temporal.ChronoUnit

class DiscordAPI {

    private val _clientId: Long
    private var _thread: Thread? = null
    private var _running: Boolean = false
    var activitySupplier: (() -> ActivityData)? = null

    constructor(clientId: Long) {
        this._clientId = clientId
    }

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
        val activity = Activity()
        if (activityData.state != null)
            activity.state = padMinLength(activityData.state!!, 2)
        if (activityData.details != null)
            activity.details = padMinLength(activityData.details!!, 2)

        if (activityData.start != null) {
            activity.timestamps().start = Instant.ofEpochMilli(activityData.start!!)
        } else if (activityData.remaining != null) {
            // TODO This is broken for some unknown reason
            activity.timestamps().clearStart()
            activity.timestamps().end = Instant.now().plusSeconds(60L).plus(3, ChronoUnit.HOURS)
        }
        activity.type = ActivityType.PLAYING
        core.activityManager().updateActivity(activity)
    }

    fun join() {
        _thread?.join()
    }

}