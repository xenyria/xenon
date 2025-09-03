/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.discord

import java.lang.System.currentTimeMillis

object DiscordTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val discordAPI = DiscordAPI(1410669620297469952)
        discordAPI.start()
        val start = currentTimeMillis()
        discordAPI.activitySupplier = {
            ActivityData(
                start = start
            )
        }

        discordAPI.join()

    }

}