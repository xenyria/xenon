package net.xenyria.xenon.forklift.network

import net.xenyria.xenon.forklift.editor.EditorClientState
import net.xenyria.xenon.protocol.IXenonPacket
import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundExitGizmoEditModePacket
import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundGizmoListPacket
import net.xenyria.xenon.protocol.clientbound.misc.ClientboundResetPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundRemoveOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundResetOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.overlay.ClientboundUpdateOverlaysPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundRemoveShapesPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundResetShapesPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundUpdateShapesPacket
import net.xenyria.xenon.protocol.clientbound.state.ClientboundAcknowledgeModeSwitchPacket

/**
 * Handles packets related to the Forklift features.
 */
object ForkliftPacketHandler {

    fun handlePacket(editorClient: EditorClientState, message: IXenonPacket): Boolean {
        when (message) {
            is ClientboundExitGizmoEditModePacket -> editorClient.exitDragMode()
            is ClientboundAcknowledgeModeSwitchPacket ->
                editorClient.acknowledgeEditMode(message.editModeEnabled)

            is ClientboundUpdateShapesPacket -> editorClient.updateShapes(message.shapes)
            is ClientboundResetShapesPacket -> editorClient.resetShapes()
            is ClientboundRemoveShapesPacket -> editorClient.removeShapes(message.shapeIds)
            is ClientboundResetPacket -> editorClient.reset()
            is ClientboundRemoveOverlaysPacket -> editorClient.removeOverlays(message.overlays)
            is ClientboundResetOverlaysPacket -> editorClient.resetOverlays()
            is ClientboundUpdateOverlaysPacket -> editorClient.updateOverlays(message.overlays)
            is ClientboundGizmoListPacket -> editorClient.updateGizmos(message.added, message.removed, message.updated)
            else -> return false
        }
        return true
    }

}