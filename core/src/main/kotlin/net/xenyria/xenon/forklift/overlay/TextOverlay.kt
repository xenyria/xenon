package net.xenyria.xenon.forklift.overlay

import net.xenyria.xenon.core.*
import net.xenyria.xenon.core.HashHelper.sha256
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Represents a text overlay.
 */
data class TextOverlay(
    var id: String,
    var opacity: Double = 1.0,
    var components: String = "",
    var anchor: OverlayAnchor = OverlayAnchor.TOP_LEFT,
    var scale: Double = 1.0,
    var offsetX: Int = 0,
    var offsetY: Int = 0
) : IHashable {

    fun writeToStream(stream: DataOutputStream) {
        stream.writeString(id)
        stream.writeByte((opacity.toFloat() * 255).toInt())
        stream.writeString(components)
        stream.writeByte(anchor.ordinal)
        stream.writeFloat(scale.toFloat())
        stream.writeVarInt(offsetX)
        stream.writeVarInt(offsetY)
    }

    private var cachedHash: String = ""
    override fun hash(): String {
        if (cachedHash.isNotBlank()) return cachedHash

        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)
        writeToStream(dos)
        cachedHash = sha256(bos.toByteArray())
        return cachedHash
    }

    companion object {
        fun fromStream(stream: DataInputStream): TextOverlay {
            return TextOverlay(
                stream.readString(),
                (stream.read() / 255.0),
                stream.readString(),
                OverlayAnchor.entries[stream.readByte().toInt()],
                stream.readFloat().toDouble(),
                stream.readVarInt(),
                stream.readVarInt()
            )
        }
    }
}
