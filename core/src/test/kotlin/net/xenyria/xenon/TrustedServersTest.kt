package net.xenyria.xenon

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrustedServersTest {
    @Test
    fun testServerMatching() {
        assertTrue(TrustedServers.isTrusted("localhost"))
        assertTrue(TrustedServers.isTrusted("127.0.0.1"))
        assertTrue(TrustedServers.isTrusted("xenyria.net"))
        assertTrue(TrustedServers.isTrusted("xenyria.net:25577"))
        assertTrue(TrustedServers.isTrusted("uwu.xenyria.net"))
        assertTrue(TrustedServers.isTrusted("uwu.xenyria.net:25565"))
        assertTrue(TrustedServers.isTrusted("xenyria.net:32767"))
        assertFalse(TrustedServers.isTrusted("xenyria.de"))
        assertFalse(TrustedServers.isTrusted("somethinglocalhostsomething"))
        assertFalse(TrustedServers.isTrusted("uwu.xenyria.de"))
        assertFalse(TrustedServers.isTrusted("127.0.0.2"))
        assertFalse(TrustedServers.isTrusted("127.0.0.12"))
        assertFalse(TrustedServers.isTrusted("my-cool-minecraft-server.xyz"))
    }
}