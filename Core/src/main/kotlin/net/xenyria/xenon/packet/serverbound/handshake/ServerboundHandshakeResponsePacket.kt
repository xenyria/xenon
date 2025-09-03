/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.serverbound.handshake

import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the server to initiate Xenon's handshake sequence.
 */
class ServerboundHandshakeResponsePacket : IXenonPacket(XenonPacketRegistry.SERVERBOUND_HANDSHAKE_RESPONSE) {

    lateinit var version: String

    override fun deserialize(input: DataInputStream) {
        version = input.readUTF()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeUTF(version)
    }

}