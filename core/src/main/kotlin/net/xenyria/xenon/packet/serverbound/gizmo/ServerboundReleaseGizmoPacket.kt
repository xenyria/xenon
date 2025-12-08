package net.xenyria.xenon.packet.serverbound.gizmo

import net.xenyria.xenon.packet.IEmptyXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.SERVERBOUND_RELEASE_GIZMO

/**
 * Sent by the client when the player stops manipulating a Gizmo.
 */
class ServerboundReleaseGizmoPacket : IEmptyXenonPacket(SERVERBOUND_RELEASE_GIZMO)