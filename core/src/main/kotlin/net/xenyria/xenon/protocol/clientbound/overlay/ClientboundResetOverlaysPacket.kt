package net.xenyria.xenon.protocol.clientbound.overlay

import net.xenyria.xenon.protocol.IEmptyXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry

/**
 * Packet that is sent by the server when all overlays should be removed.
 */
class ClientboundResetOverlaysPacket : IEmptyXenonPacket(XenonPacketRegistry.CLIENTBOUND_RESET_OVERLAYS)