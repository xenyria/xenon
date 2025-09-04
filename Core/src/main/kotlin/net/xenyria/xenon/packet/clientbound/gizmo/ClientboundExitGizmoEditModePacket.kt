package net.xenyria.xenon.packet.clientbound.gizmo

import net.xenyria.xenon.packet.IEmptyXenonPacket
import net.xenyria.xenon.packet.XenonPacketRegistry.CLIENTBOUND_EXIT_GIZMO_EDIT_MODE

/**
 * Sent by the server to force the player from editing the current Gizmo
 * (e.g. the object got deleted, the player got teleported to a different world, ...)
 */
class ClientboundExitGizmoEditModePacket() : IEmptyXenonPacket(CLIENTBOUND_EXIT_GIZMO_EDIT_MODE)