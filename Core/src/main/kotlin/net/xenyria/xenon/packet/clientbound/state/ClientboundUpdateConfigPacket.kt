/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.clientbound.state

import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.config.deserializeConfig
import net.xenyria.xenon.forklift.config.serializeConfig
import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the server to set Forklift's config.
 */
class ClientboundUpdateConfigPacket : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_UPDATE_CONFIG) {

    lateinit var config: ForkliftConfig
        private set

    override fun deserialize(input: DataInputStream) {
        val length = input.readInt()
        val result = deserializeConfig(input.readNBytes(length))
        config = result.getOrThrow()
    }

    override fun serialize(output: DataOutputStream) {
        val bytes = serializeConfig(config)
        output.writeInt(bytes.size)
        output.write(bytes)
    }


}