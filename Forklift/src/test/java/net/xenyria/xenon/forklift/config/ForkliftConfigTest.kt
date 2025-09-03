/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.forklift.config

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ForkliftConfigTest {

    @Test
    fun testConfigSerialization() {
        val config = ForkliftConfig(
            translationGridSnap = 123.45,
            scaleGridSnap = 420.69,
            rotationGridSnap = 111.222
        )

        val bytes = serializeConfig(config)
        val restored = deserializeConfig(bytes)
        assertTrue(restored.isSuccess)
        assertEquals(config, restored.getOrThrow())
    }

    @Test
    fun testConfigEquals() {
        val configA = ForkliftConfig(translationGridSnap = 123.45, scaleGridSnap = 420.69, rotationGridSnap = 111.222)
        val configB = ForkliftConfig(translationGridSnap = 123.0, scaleGridSnap = 420.69, rotationGridSnap = 111.0)
        assertEquals(configA, configA)
        assertEquals(configA.hashCode(), configA.hashCode())
        assertNotEquals(configA, configB)
        assertNotEquals(configA.hashCode(), configB.hashCode())
    }
}