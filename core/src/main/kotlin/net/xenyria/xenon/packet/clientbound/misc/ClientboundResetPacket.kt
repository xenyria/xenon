package net.xenyria.xenon.packet.clientbound.misc

import net.xenyria.xenon.packet.IEmptyXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry

/**
 * Empty packet, sent by the server to reset the state of Forklift (e.g. switching worlds, joining a different server)
 */
class ClientboundResetPacket : IEmptyXenonPacket(XenonPacketRegistry.CLIENTBOUND_RESET)