/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.packet.serverbound.gizmo

import net.xenyria.xenon.packet.IEmptyXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.SERVERBOUND_RELEASE_GIZMO

/**
 * Sent by the client when the player stops manipulating a Gizmo.
 */
class ServerboundReleaseGizmoPacket : IEmptyXenonPacket(SERVERBOUND_RELEASE_GIZMO)