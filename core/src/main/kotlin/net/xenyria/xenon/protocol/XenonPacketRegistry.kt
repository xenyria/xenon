package net.xenyria.xenon.protocol

import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundExitGizmoEditModePacket
import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundGizmoListPacket
import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundUpdateGizmoPacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeResponsePacket
import net.xenyria.xenon.protocol.clientbound.handshake.ClientboundHandshakeStartPacket
import net.xenyria.xenon.protocol.clientbound.misc.ClientboundResetPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundRemoveOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundResetOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundUpdateOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundRemoveShapesPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundResetShapesPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundUpdateShapesPacket
import net.xenyria.xenon.protocol.clientbound.state.ClientboundAcknowledgeModeSwitchPacket
import net.xenyria.xenon.protocol.clientbound.state.ClientboundUpdateConfigPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundReleaseGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundUpdateGizmoPacket
import net.xenyria.xenon.protocol.serverbound.handshake.ServerboundHandshakeRequestPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundRequestModeSwitchPacket
import net.xenyria.xenon.protocol.serverbound.state.ServerboundUpdateSelectionPacket
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object XenonPacketRegistry {

    private val _registeredPackets = HashMap<Int, net.xenyria.xenon.protocol.RegisteredPacket>()
    private val _lock = ReentrantReadWriteLock()

    fun makePacketType(name: String, constructor: () -> net.xenyria.xenon.protocol.IXenonPacket): net.xenyria.xenon.protocol.XenonPacketType {
        _lock.write {
            val id = _registeredPackets.size
            val type = XenonPacketType(id, name)
            _registeredPackets[id] = RegisteredPacket(constructor)
            return type
        }
    }

    fun createEmpty(id: Int): net.xenyria.xenon.protocol.IXenonPacket {
        _lock.read {
            val entry = _registeredPackets[id]
            requireNotNull(entry) { "No packet registered for id $id" }
            return entry.constructor()
        }
    }

    // Common packets
    val CLIENTBOUND_HANDSHAKE_START = makePacketType("handshake_start", ::ClientboundHandshakeStartPacket)
    val SERVERBOUND_HANDSHAKE_REQUEST = makePacketType("handshake_request", ::ServerboundHandshakeRequestPacket)
    val CLIENTBOUND_HANDSHAKE_RESPONSE = makePacketType("handshake_response", ::ClientboundHandshakeResponsePacket)

    // Level editor / Forklift packets
    val CLIENTBOUND_ACKNOWLEDGE_MODE_SWITCH = makePacketType("acknowledge_mode_switch", ::ClientboundAcknowledgeModeSwitchPacket)
    val CLIENTBOUND_RESET = makePacketType("reset", ::ClientboundResetPacket)
    val CLIENTBOUND_UPDATE_CONFIG = makePacketType("update_config", ::ClientboundUpdateConfigPacket)
    val CLIENTBOUND_GIZMO_LIST = makePacketType("gizmo_list", ::ClientboundGizmoListPacket)
    val CLIENTBOUND_UPDATE_GIZMO = makePacketType("update_gizmo", ::ClientboundUpdateGizmoPacket)
    val CLIENTBOUND_EXIT_GIZMO_EDIT_MODE = makePacketType("exit_gizmo_edit_mode", ::ClientboundExitGizmoEditModePacket)
    val SERVERBOUND_REQUEST_MODE_SWITCH = makePacketType("request_mode_switch", ::ServerboundRequestModeSwitchPacket)
    val SERVERBOUND_UPDATE_SELECTION = makePacketType("update_selection", ::ServerboundUpdateSelectionPacket)
    val SERVERBOUND_RELEASE_GIZMO = makePacketType("release_gizmo", ::ServerboundReleaseGizmoPacket)
    val SERVERBOUND_REQUEST_GIZMO = makePacketType("request_gizmo", ::ServerboundRequestGizmoPacket)
    val SERVERBOUND_UPDATE_GIZMO = makePacketType("update_gizmo", ::ServerboundUpdateGizmoPacket)

    // Forklift / Debug Shapes
    val CLIENTBOUND_REMOVE_SHAPES = makePacketType("remove_shapes", ::ClientboundRemoveShapesPacket)
    val CLIENTBOUND_UPDATE_SHAPES = makePacketType("update_shapes", ::ClientboundUpdateShapesPacket)
    val CLIENTBOUND_RESET_SHAPES = makePacketType("reset_shapes", ::ClientboundResetShapesPacket)

    // Forklift / Overlays
    val CLIENTBOUND_RESET_OVERLAYS = makePacketType("reset_overlays", ::ClientboundResetOverlaysPacket)
    val CLIENTBOUND_REMOVE_OVERLAYS = makePacketType("remove_overlays", ::ClientboundRemoveOverlaysPacket)
    val CLIENTBOUND_UPDATE_OVERLAYS = makePacketType("update_overlays", ::ClientboundUpdateOverlaysPacket)
}

private class RegisteredPacket(val constructor: () -> net.xenyria.xenon.protocol.IXenonPacket)
