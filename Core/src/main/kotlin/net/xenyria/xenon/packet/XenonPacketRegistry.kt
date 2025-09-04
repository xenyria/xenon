package net.xenyria.xenon.packet

import net.xenyria.xenon.packet.clientbound.gizmo.ClientboundEnterGizmoEditModePacket
import net.xenyria.xenon.packet.clientbound.gizmo.ClientboundExitGizmoEditModePacket
import net.xenyria.xenon.packet.clientbound.gizmo.ClientboundGizmoListPacket
import net.xenyria.xenon.packet.clientbound.handshake.ClientboundHandshakePacket
import net.xenyria.xenon.packet.clientbound.misc.ClientboundResetPacket
import net.xenyria.xenon.packet.clientbound.shape.ClientboundRemoveShapesPacket
import net.xenyria.xenon.packet.clientbound.shape.ClientboundUpdateShapesPacket
import net.xenyria.xenon.packet.clientbound.state.ClientboundAcknowledgeModeSwitchPacket
import net.xenyria.xenon.packet.clientbound.state.ClientboundUpdateConfigPacket
import net.xenyria.xenon.packet.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.packet.serverbound.gizmo.ServerboundRequestGizmoPacket
import net.xenyria.xenon.packet.serverbound.gizmo.ServerboundUpdateGizmoPacket
import net.xenyria.xenon.packet.serverbound.handshake.ServerboundHandshakeResponsePacket
import net.xenyria.xenon.packet.serverbound.state.ServerboundRequestModeSwitchPacket
import net.xenyria.xenon.packet.serverbound.state.ServerboundUpdateSelectionPacket
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object XenonPacketRegistry {

    private val _registeredPackets = HashMap<Int, RegisteredPacket>()
    private val _lock = ReentrantReadWriteLock()

    fun makePacketType(name: String, constructor: () -> IXenonPacket): XenonPacketType {
        _lock.write {
            val id = _registeredPackets.size
            val type = XenonPacketType(id, name)
            _registeredPackets[id] = RegisteredPacket(constructor)
            return type
        }
    }

    fun createEmpty(id: Int): IXenonPacket {
        _lock.read {
            val entry = _registeredPackets[id]
            requireNotNull(entry) { "No packet registered for id $id" }
            return entry.constructor()
        }
    }

    // Common packets
    val CLIENTBOUND_HANDSHAKE = makePacketType("handshake", ::ClientboundHandshakePacket)
    val SERVERBOUND_HANDSHAKE_RESPONSE = makePacketType("handshake_response", ::ServerboundHandshakeResponsePacket)

    // Level editor / Forklift packets
    val CLIENTBOUND_ACKNOWLEDGE_MODE_SWITCH = makePacketType("acknowledge_mode_switch", ::ClientboundAcknowledgeModeSwitchPacket)
    val CLIENTBOUND_RESET = makePacketType("reset", ::ClientboundResetPacket)
    val CLIENTBOUND_UPDATE_CONFIG = makePacketType("update_config", ::ClientboundUpdateConfigPacket)
    val CLIENTBOUND_GIZMO_LIST = makePacketType("gizmo_list", ::ClientboundGizmoListPacket)
    val CLIENTBOUND_ENTER_GIZMO_EDIT_MODE = makePacketType("enter_gizmo_edit_mode", ::ClientboundEnterGizmoEditModePacket)
    val CLIENTBOUND_EXIT_GIZMO_EDIT_MODE = makePacketType("exit_gizmo_edit_mode", ::ClientboundExitGizmoEditModePacket)
    val SERVERBOUND_REQUEST_MODE_SWITCH = makePacketType("request_mode_switch", ::ServerboundRequestModeSwitchPacket)
    val SERVERBOUND_UPDATE_SELECTION = makePacketType("update_selection", ::ServerboundUpdateSelectionPacket)
    val SERVERBOUND_RELEASE_GIZMO = makePacketType("release_gizmo", ::ServerboundReleaseGizmoPacket)
    val SERVERBOUND_REQUEST_GIZMO = makePacketType("request_gizmo", ::ServerboundRequestGizmoPacket)
    val SERVERBOUND_UPDATE_GIZMO = makePacketType("update_gizmo", ::ServerboundUpdateGizmoPacket)

    // Forklift / Debug Shapes
    val CLIENTBOUND_REMOVE_SHAPES = makePacketType("remove_shapes", ::ClientboundRemoveShapesPacket)
    val CLIENTBOUND_UPDATE_SHAPES = makePacketType("update_shapes", ::ClientboundUpdateShapesPacket)

}

private class RegisteredPacket(val constructor: () -> IXenonPacket)
