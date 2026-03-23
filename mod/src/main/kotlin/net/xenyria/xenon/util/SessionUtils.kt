package net.xenyria.xenon.util

import net.xenyria.xenon.game

fun getCurrentServer(): String {
    val server = game.currentServer ?: return ""
    val ip = server.ip
    val colons = ip.count { it == ':' }
    if (colons == 1) {
        val split = ip.split(":")
        return split[0]
    }
    return ip
}
