@file:Suppress("UNCHECKED_CAST")

package net.xenyria.xenon.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.xenyria.xenon.forklift.network.ForkliftPacketHandler
import net.xenyria.xenon.forklift.network.XenonPacketHandler
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.parsePacket
import net.xenyria.xenon.xenon
import java.io.ByteArrayInputStream

fun interface IPacketListener<Type : IXenonPacket> {
    fun onPacket(packet: Type)
}

object XenonPacketListener {

    fun initialize() {
        PayloadTypeRegistry.playC2S().register(XenonPayload.ID, XenonPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(XenonPayload.ID, XenonPayload.CODEC)

        ClientPlayConnectionEvents.INIT.register { _, _ ->
            xenon.getForkliftOrNull()?.reset()
        }
        ClientPlayNetworking.registerGlobalReceiver(XenonPayload.ID, { data, ctx ->
            val packet = parsePacket(ByteArrayInputStream(data.bytes))

            val xenon = xenon
            val editor = xenon.getForkliftOrNull()?.editor

            if (XenonPacketHandler.handlePacket(xenon.client, packet)) return@registerGlobalReceiver
            if (editor != null && ForkliftPacketHandler.handlePacket(editor, packet)) return@registerGlobalReceiver
        })
    }

}