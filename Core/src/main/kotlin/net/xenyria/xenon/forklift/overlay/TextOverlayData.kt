/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.forklift.overlay

import net.xenyria.xenon.core.IHashable
import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.sha256
import net.xenyria.xenon.core.writeVarInt
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

data class TextOverlayData(
    val id: String,
    val opacity: Double,
    val components: String,
    val anchor: OverlayAnchor,
    val scale: Double,
    val offsetX: Int,
    val offsetY: Int
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
