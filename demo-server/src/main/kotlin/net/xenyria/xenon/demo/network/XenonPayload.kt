package net.xenyria.xenon.demo.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.xenyria.xenon.MOD_ID

class XenonPayload() : CustomPacketPayload {

    lateinit var bytes: ByteArray
        private set

    companion object {
        val CODEC = CustomPacketPayload.codec(
            { obj: XenonPayload, buf: ByteBuf -> obj.write(buf) },
            { XenonPayload() }
        )
        val ID = CustomPacketPayload.createType<XenonPayload>(MOD_ID)
    }

    constructor(bytes: ByteArray) : this() {
        this.bytes = bytes
    }

    private fun write(buf: ByteBuf) {
        buf.writeBytes(bytes)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

}