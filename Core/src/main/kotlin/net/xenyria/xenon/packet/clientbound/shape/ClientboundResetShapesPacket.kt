package net.xenyria.xenon.packet.clientbound.shape

import net.xenyria.xenon.packet.IEmptyXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry

/**
 * Empty packet, sent by the server to reset all shapes rendered by Forklift (e.g. switching worlds, joining a different server)
 */
class ClientboundResetShapesPacket : IEmptyXenonPacket(XenonPacketRegistry.CLIENTBOUND_RESET_SHAPES)