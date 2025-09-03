/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.forklift.config

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

const val CONFIG_FORMAT_VERSION = 1

class ForkliftConfig(
    translationGridSnap: Double = 0.0,
    scaleGridSnap: Double = 0.0,
    rotationGridSnap: Double = 0.0
) {
    var translationGridSnap: Double = translationGridSnap
        private set

    var scaleGridSnap: Double = scaleGridSnap
        private set

    var rotationGridSnap: Double = rotationGridSnap
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ForkliftConfig

        if (translationGridSnap != other.translationGridSnap) return false
        if (scaleGridSnap != other.scaleGridSnap) return false
        if (rotationGridSnap != other.rotationGridSnap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translationGridSnap.hashCode()
        result = 31 * result + scaleGridSnap.hashCode()
        result = 31 * result + rotationGridSnap.hashCode()
        return result
    }
}

fun serializeConfig(input: ForkliftConfig): ByteArray {
    val output = ByteArrayOutputStream()
    val stream = DataOutputStream(output)
    stream.writeInt(CONFIG_FORMAT_VERSION)
    stream.writeDouble(input.translationGridSnap)
    stream.writeDouble(input.scaleGridSnap)
    stream.writeDouble(input.rotationGridSnap)
    return output.toByteArray()
}

fun deserializeConfig(input: ByteArray): Result<ForkliftConfig> {
    return runCatching {
        val reader = DataInputStream(ByteArrayInputStream(input))
        val version = reader.readInt()
        check(version == CONFIG_FORMAT_VERSION) { "Unsupported config version: $version" }

        ForkliftConfig(
            translationGridSnap = reader.readDouble(),
            scaleGridSnap = reader.readDouble(),
            rotationGridSnap = reader.readDouble()
        )
    }
}