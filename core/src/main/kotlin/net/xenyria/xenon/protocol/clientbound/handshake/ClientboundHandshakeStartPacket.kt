package net.xenyria.xenon.protocol.clientbound.handshake

import net.xenyria.xenon.protocol.IEmptyXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry

/**
 * Packet sent by the client to initiate the handshake process.
 */
class ClientboundHandshakeStartPacket : IEmptyXenonPacket(XenonPacketRegistry.CLIENTBOUND_HANDSHAKE_START)