/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.core

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

private const val HEX_RADIX = 16
private const val HASH_BUFFER_SIZE = 32 * 1024

private fun createStringFromDigest(digest: ByteArray): String {
    return BigInteger(1, digest).toString(HEX_RADIX)
}

fun hash(input: InputStream, digest: String): String {
    val digest = MessageDigest.getInstance(digest)
    val buffer = ByteArray(HASH_BUFFER_SIZE)
    var length: Int
    while (input.read(buffer).also { length = it } > 0) {
        digest.update(buffer, 0, length)
    }
    return createStringFromDigest(digest.digest())
}

fun sha256(bytes: ByteArray): String {
    return hash(ByteArrayInputStream(bytes), "SHA-256")
}

interface IHashable {

    fun hash(): String

}