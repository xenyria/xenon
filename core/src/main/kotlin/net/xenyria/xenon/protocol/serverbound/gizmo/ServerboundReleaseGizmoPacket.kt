package net.xenyria.xenon.protocol.serverbound.gizmo

import net.xenyria.xenon.protocol.IEmptyXenonPacket
import net.xenyria.xenon.protocol.XenonPacketRegistry.SERVERBOUND_RELEASE_GIZMO

/**
 * Sent by the client when the player stops manipulating a Gizmo.
 */
class ServerboundReleaseGizmoPacket : IEmptyXenonPacket(SERVERBOUND_RELEASE_GIZMO)