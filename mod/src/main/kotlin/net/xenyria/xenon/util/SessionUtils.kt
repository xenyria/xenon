package net.xenyria.xenon.util

import net.xenyria.xenon.game

/**
 * Helper function for obtaining the current server IP address.
 */
fun getCurrentServer(): String {
    val server = game.currentServer ?: return ""
    val ip = server.ip

    // We expect either:
    // - An IPv4 address with an optional port at the end
    // - A generic hostname with an optional port at the end
    // IPV6 addresses are not supported by this yet.
    val colons = ip.count { it == ':' }
    if (colons == 1) {
        val split = ip.split(":")
        return split[0]
    }
    return ip
}
