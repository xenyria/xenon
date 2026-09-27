package net.xenyria.xenon.discord

import net.xenyria.xenon.core.readOptional
import net.xenyria.xenon.core.readString
import net.xenyria.xenon.core.writeOptional
import net.xenyria.xenon.core.writeString
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Represents activity information for the Discord Rich Presence/Activity feature.
 */
data class ActivityData(
    val state: String? = null,
    val details: String? = null,
    val start: Long? = null,
    val remaining: Int? = null
) {
    companion object {
        fun write(data: ActivityData, stream: DataOutputStream) {
            stream.writeOptional(data.state) { stream.writeString(it) }
            stream.writeOptional(data.details) { stream.writeString(it) }
            stream.writeOptional(data.start) { stream.writeLong(it) }
            stream.writeOptional(data.remaining) { stream.writeInt(it) }
        }

        fun read(stream: DataInputStream): ActivityData {
            return ActivityData(
                state = stream.readOptional { it.readString() },
                details = stream.readOptional { it.readString() },
                start = stream.readOptional { it.readLong() },
                remaining = stream.readOptional { it.readInt() }
            )
        }
    }
}

