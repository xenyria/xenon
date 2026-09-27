package net.xenyria.xenon

import java.nio.charset.StandardCharsets

data class TrustedServer(val regex: Regex)

/**
 * Holds an in-memory list of all servers that are considered "trusted".
 * This is used for checking if Discord Activity can be set by the current server.
 */
object TrustedServers {

    private val servers = ArrayList<TrustedServer>()

    fun isTrusted(hostname: String): Boolean {
        val hostname = hostname.trim()
        for ((regex) in servers) {
            if (regex.containsMatchIn(hostname)) return true
        }
        return false
    }

    init {
        val trustedList = TrustedServer::class.java.getResourceAsStream("/xenon/trusted_servers.txt")
        requireNotNull(trustedList) { "Unable to find trusted servers list for Xenon" }
        String(trustedList.readAllBytes(), StandardCharsets.UTF_8).lines().forEach { line ->
            servers.add(TrustedServer(line.toRegex()))
        }
    }

}