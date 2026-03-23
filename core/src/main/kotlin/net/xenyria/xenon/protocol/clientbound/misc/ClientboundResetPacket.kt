package net.xenyria.xenon.protocol.clientbound.misc

import net.xenyria.xenon.protocol.IEmptyXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry

/**
 * Empty packet, sent by the server to reset the state of Forklift (e.g. switching worlds, joining a different server)
 */
class ClientboundResetPacket : IEmptyXenonPacket(XenonPacketRegistry.CLIENTBOUND_RESET)