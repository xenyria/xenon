/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.clientbound.handshake

import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the server to initiate Xenon's handshake sequence.
 */
class ClientboundHandshakePacket : IXenonPacket(XenonPacketRegistry.CLIENTBOUND_HANDSHAKE) {

    var canUseEditMode: Boolean = false // Level editor mode feature flag
        private set

    override fun deserialize(input: DataInputStream) {
        canUseEditMode = input.readBoolean()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeBoolean(canUseEditMode)
    }

}