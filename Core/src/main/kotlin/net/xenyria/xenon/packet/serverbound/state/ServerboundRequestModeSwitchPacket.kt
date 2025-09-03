/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.serverbound.state

import net.xenyria.xenon.packet.IXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.SERVERBOUND_REQUEST_MODE_SWITCH
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Sent by the client to enter/leave edit mode.
 */
class ServerboundRequestModeSwitchPacket() : IXenonPacket(SERVERBOUND_REQUEST_MODE_SWITCH) {

    constructor(desiredState: Boolean) : this() {
        this.desiredState = desiredState
    }

    var desiredState: Boolean = false
        private set

    override fun deserialize(input: DataInputStream) {
        desiredState = input.readBoolean()
    }

    override fun serialize(output: DataOutputStream) {
        output.writeBoolean(desiredState)
    }

}