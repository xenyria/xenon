package net.xenyria.xenon.forklift.network

import net.xenyria.xenon.forklift.editor.Editor
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

object ForkliftPacketHandler {

    fun handlePacket(editor: Editor, message: IXenonPacket): Boolean {
        when (message) {
            is ClientboundExitGizmoEditModePacket -> editor.exitDragMode()
            is ClientboundAcknowledgeModeSwitchPacket -> editor.acknowledgeEditMode(message.editModeEnabled)
            is ClientboundUpdateShapesPacket -> editor.updateShapes(message.shapes)
            is ClientboundResetShapesPacket -> editor.resetShapes()
            is ClientboundRemoveShapesPacket -> editor.removeShapes(message.shapeIds)
            is ClientboundResetPacket -> editor.reset()
            is ClientboundRemoveOverlaysPacket -> editor.removeOverlays(message.overlays)
            is ClientboundResetOverlaysPacket -> editor.resetOverlays()
            is ClientboundUpdateOverlaysPacket -> editor.updateOverlays(message.overlays)
            is ClientboundGizmoListPacket -> editor.updateGizmos(message.added, message.removed, message.updated)
            else -> return false
        }
        return true
    }

}