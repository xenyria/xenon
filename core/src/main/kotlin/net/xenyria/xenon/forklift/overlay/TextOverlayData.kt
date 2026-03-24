package net.xenyria.xenon.forklift.overlay

import net.xenyria.xenon.core.IHashable
import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.sha256
import net.xenyria.xenon.core.writeVarInt
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

data class TextOverlayData(
    var id: String,
    var opacity: Double = 1.0,
    var components: String = "",
    var anchor: OverlayAnchor = OverlayAnchor.TOP_LEFT,
    var scale: Double = 1.0,
    var offsetX: Int = 0,
    var offsetY: Int = 0
) : IHashable {

    fun writeToStream(stream: DataOutputStream) {
        stream.writeUTF(id)
        stream.writeFloat(opacity.toFloat())
        stream.writeUTF(components)
        stream.writeByte(anchor.ordinal)
        stream.writeFloat(scale.toFloat())
        stream.writeVarInt(offsetX)
        stream.writeVarInt(offsetY)
    }

    private var _cachedHash: String = ""
    override fun hash(): String {
        if (!_cachedHash.isBlank()) return _cachedHash
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)
        writeToStream(dos)
        _cachedHash = sha256(bos.toByteArray())
        return _cachedHash
    }

    companion object {
        fun fromStream(stream: DataInputStream): TextOverlayData {
            return TextOverlayData(
                stream.readUTF(),
                stream.readFloat().toDouble(),
                stream.readUTF(),
                OverlayAnchor.entries[stream.readByte().toInt()],
                stream.readFloat().toDouble(),
                stream.readVarInt(),
                stream.readVarInt()
            )
        }
    }
}
