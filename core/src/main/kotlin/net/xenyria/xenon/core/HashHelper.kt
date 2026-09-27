package net.xenyria.xenon.core

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

private const val HEX_RADIX = 16
private const val HASH_BUFFER_SIZE = 32 * 1024

object HashHelper {
    private fun createStringFromDigest(digest: ByteArray): String {
        return BigInteger(1, digest).toString(HEX_RADIX)
    }

    /**
     * Creates a hash of the given input stream using the specified digest algorithm.
     */
    fun hash(input: InputStream, digest: String): String {
        val digest = MessageDigest.getInstance(digest)
        val buffer = ByteArray(HASH_BUFFER_SIZE)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            digest.update(buffer, 0, length)
        }
        return createStringFromDigest(digest.digest())
    }

    /**
     * Creates a SHA-256 hash of the given byte array.
     */
    fun sha256(bytes: ByteArray): String {
        return hash(ByteArrayInputStream(bytes), "SHA-256")
    }

}

/**
 * Interface for classes that can output a hash of their state.
 */
interface IHashable {
    fun hash(): String
}