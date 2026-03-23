package net.xenyria.xenon.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.xenyria.xenon.CHANNEL_ID

class XenonPayload() : CustomPacketPayload {

    lateinit var bytes: ByteArray
        private set

    companion object {
        val CODEC = CustomPacketPayload.codec(
            { obj: XenonPayload, buf: ByteBuf -> obj.write(buf) },
            { XenonPayload(it) }
        )
        val ID = CustomPacketPayload.Type<XenonPayload>(Identifier.parse(CHANNEL_ID))
    }

    constructor(bytes: ByteArray) : this() {
        this.bytes = bytes
    }

    constructor(buf: ByteBuf) : this() {
        val bytes = ByteArray(buf.readableBytes())
        buf.readBytes(bytes)
        this.bytes = bytes
    }

    private fun write(buf: ByteBuf) {
        buf.writeBytes(bytes)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

}