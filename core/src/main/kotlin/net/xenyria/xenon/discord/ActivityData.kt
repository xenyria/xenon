package net.xenyria.xenon.discord

import net.xenyria.xenon.core.readOptional
import net.xenyria.xenon.core.writeOptional
import java.io.DataInputStream
import java.io.DataOutputStream

data class ActivityData(
    val state: String? = null,
    val details: String? = null,
    val start: Long? = null,
    val remaining: Int? = null
) {
    companion object {
        fun write(data: ActivityData, stream: DataOutputStream) {
            stream.writeOptional(data.state) { stream.writeUTF(it) }
            stream.writeOptional(data.details) { stream.writeUTF(it) }
            stream.writeOptional(data.start) { stream.writeLong(it) }
            stream.writeOptional(data.remaining) { stream.writeInt(it) }
        }

        fun read(stream: DataInputStream): ActivityData {
            return ActivityData(
                state = stream.readOptional { it.readUTF() },
                details = stream.readOptional { it.readUTF() },
                start = stream.readOptional { it.readLong() },
                remaining = stream.readOptional { it.readInt() }
            )
        }
    }
}

