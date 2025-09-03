/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.server

import net.xenyria.xenon.core.Vec3D
import net.xenyria.xenon.packet.IXenonPacket

interface IXenonClient {

    val cameraPosition: Vec3D

    fun sendPluginMessage(channel: String, data: ByteArray)

    fun sendXenonMessage(packet: IXenonPacket) {

    }

}