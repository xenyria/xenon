package net.xenyria.xenon.demo.feature.gizmo

import net.xenyria.xenon.demo.player.XenonPlayer
import net.xenyria.xenon.demo.player.XenonPlayerManager
import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundExitGizmoEditModePacket
import net.xenyria.xenon.protocol.clientbound.gizmo.ClientboundGizmoListPacket
import net.xenyria.xenon.protocol.serverbound.gizmo.ServerboundRequestGizmoPacket
import org.bukkit.entity.Player
import org.joml.Vector3dc
import java.util.*

object XenonGizmos {

    private val _entities = ArrayList<EditorEntity>()

    // Maps GizmoID to a player ID
    private val _editors = HashMap<UUID, UUID>()

    fun getEditor(gizmoId: UUID): UUID? {
        return _editors[gizmoId]
    }

    fun removeEditor(playerId: UUID) {
        _editors.entries.removeIf { it.value == playerId }
        emitFullUpdate()
    }

    fun addEntity(entity: EditorEntity) {
        _entities.add(entity)
        XenonPlayerManager.activePlayers.forEach { player ->
            player.sendXenonMessage(
                ClientboundGizmoListPacket(
                    emptyList(),
                    listOf(entity.toGizmoData()),
                    emptyList()
                )
            )
        }
    }

    fun removeEntity(entity: EditorEntity) {
        _entities.removeIf { it.id == entity.id }
        _editors.remove(entity.id)
        entity.onRemove()
        XenonPlayerManager.activePlayers.forEach { player ->
            player.sendXenonMessage(
                ClientboundGizmoListPacket(
                    emptyList(),
                    emptyList(),
                    listOf(entity.id)
                )
            )
        }
    }

    fun spawnAll(client: XenonPlayer) {
        client.sendXenonMessage(
            ClientboundGizmoListPacket(
                emptyList(),
                _entities.map { it.toGizmoData() },
                emptyList()
            )
        )
    }

    fun emitUpdate(entity: EditorEntity) {
        XenonPlayerManager.activePlayers.forEach { player ->
            player.sendXenonMessage(
                ClientboundGizmoListPacket(
                    listOf(entity.toGizmoData()),
                    emptyList(),
                    emptyList()
                )
            )
        }
    }

    fun emitFullUpdate() {
        XenonPlayerManager.activePlayers.forEach { player ->
            player.sendXenonMessage(
                ClientboundGizmoListPacket(
                    _entities.map { it.toGizmoData() },
                    emptyList(),
                    emptyList()
                )
            )
        }
    }

    fun updateGizmo(player: Player, gizmoId: UUID, position: Vector3dc, rotation: Vector3dc, scale: Vector3dc) {
        if (getEditor(gizmoId) != player.uniqueId) return
        val entity = _entities.find { it.id == gizmoId } ?: return
        entity.update(position, scale, rotation)
    }

    fun onRequestGizmo(client: XenonPlayer, message: ServerboundRequestGizmoPacket) {
        val entity = _entities.find { it.id == message.gizmoId }
        val currentEditor = getEditor(message.gizmoId)
        if (entity == null || (currentEditor != null && getEditor(message.gizmoId) != client.player.uniqueId)) {
            client.sendXenonMessage(ClientboundExitGizmoEditModePacket())
            if (entity != null) {
                client.sendXenonMessage(
                    ClientboundGizmoListPacket(
                        listOf(entity.toGizmoData()), emptyList(), emptyList()
                    )
                )
            }
            return
        }
        _editors[message.gizmoId] = client.player.uniqueId
    }

    fun onReleaseGizmo(client: XenonPlayer) {
        removeEditor(client.player.uniqueId)
    }
}