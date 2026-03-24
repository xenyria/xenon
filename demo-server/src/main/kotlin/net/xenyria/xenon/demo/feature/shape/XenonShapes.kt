package net.xenyria.xenon.demo.feature.shape

import net.xenyria.xenon.demo.player.XenonPlayer
import net.xenyria.xenon.demo.player.XenonPlayerManager
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundRemoveShapesPacket
import net.xenyria.xenon.protocol.clientbound.shape.ClientboundUpdateShapesPacket
import net.xenyria.xenon.shape.IEditorShape

object XenonShapes {

    private val _shapes = ArrayList<IEditorShape<*>>()

    fun spawnAll(player: XenonPlayer) {
        player.sendXenonMessage(ClientboundUpdateShapesPacket(_shapes))
    }

    fun add(shape: IEditorShape<*>) {
        _shapes.add(shape)
        XenonPlayerManager.activePlayers.forEach {
            it.sendXenonMessage(ClientboundUpdateShapesPacket(listOf(shape)))
        }
    }

    fun emitUpdate() {
        XenonPlayerManager.activePlayers.forEach {
            it.sendXenonMessage(ClientboundUpdateShapesPacket(_shapes))
        }
    }

    fun remove(shape: IEditorShape<*>) {
        _shapes.remove(shape)
        XenonPlayerManager.activePlayers.forEach {
            it.sendXenonMessage(ClientboundRemoveShapesPacket(listOf(shape.id)))
        }
    }

    fun addAll(shapes: List<IEditorShape<*>>) {
        _shapes.addAll(shapes)
        emitUpdate()
    }

}