@file:Suppress("SameParameterValue")

package net.xenyria.xenon.discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import java.time.Instant

/**
 * Wrapper for the Discord Game SDK to manage the user's Discord activity.
 */
class DiscordAPI {

    private val clientId: Long
    private var thread: Thread? = null
    private var running: Boolean = false
    private var lastActivity: ActivityData? = null
    var activitySupplier: (() -> ActivityData)? = null

    constructor(clientId: Long) {
        this.clientId = clientId
    }

    @Synchronized
    fun start() {
        running = false
        thread?.join()
        running = true
        val newThread = Thread({
            CreateParams().use {
                it.clientID = clientId
                it.flags = CreateParams.getDefaultFlags()
                Core(it).use { core ->
                    while (running) {
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
        }, "Xenon Discord Activity Thread")
        thread = newThread
        newThread.start()
    }

    private fun padMinLength(
        input: String,
        minLength: Int
    ): String {
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
        if (activityData == lastActivity) return

        val activity = Activity()
        if (activityData.state != null)
            activity.state = padMinLength(activityData.state!!, 2)
        if (activityData.details != null)
            activity.details = padMinLength(activityData.details!!, 2)

        if (activityData.start != null) {
            activity.timestamps().start = Instant.ofEpochMilli(activityData.start!!)
        }
        activity.type = ActivityType.PLAYING
        lastActivity = activityData
        core.activityManager().updateActivity(activity)
    }

    @Synchronized
    fun stop() {
        running = false
        thread?.interrupt()
        thread?.join()
        thread = null
    }

}