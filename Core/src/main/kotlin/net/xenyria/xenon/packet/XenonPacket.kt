/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet

import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IXenonPacket(val type: XenonPacketType) {

    abstract fun deserialize(input: DataInputStream)

    abstract fun serialize(output: DataOutputStream)

}

abstract class IEmptyXenonPacket(type: XenonPacketType) : IXenonPacket(type) {
    override fun deserialize(input: DataInputStream) {
    }

    override fun serialize(output: DataOutputStream) {
    }
}