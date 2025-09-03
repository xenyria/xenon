/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.clientbound.misc

import net.xenyria.xenon.packet.IEmptyXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry

/**
 * Empty packet, sent by the server to reset the state of Forklift (e.g. switching worlds, joining a different server)
 */
class ClientboundResetPacket : IEmptyXenonPacket(XenonPacketRegistry.CLIENTBOUND_RESET)