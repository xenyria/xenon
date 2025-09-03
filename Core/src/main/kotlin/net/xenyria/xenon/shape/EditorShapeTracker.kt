/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape

internal class TrackedShape(val shape: IEditorShape<*>, var lastHash: Long) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackedShape

        if (lastHash != other.lastHash) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lastHash.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }
}

class EditorShapeTracker(val receiver: ShapeNotificationReceiver) {

    private var _trackedShapes = HashMap<String, TrackedShape>()

    companion object {
        const val VIEW_DISTANCE = 256.0
    }

    @Synchronized
    fun clear() {
        if (_trackedShapes.isEmpty()) return
        _trackedShapes.clear()
        receiver.clearShapes()
    }

    @Synchronized
    fun isTrackingShape(id: String): Boolean {
        return _trackedShapes.containsKey(id)
    }

    @Synchronized
    fun updateExistingShapes() {
        val shapesToUpdate = ArrayList<IEditorShape<*>>()
        this._trackedShapes.forEach {
            val hash = it.value.shape.hash
            if (it.value.lastHash != hash) {
                shapesToUpdate.add(it.value.shape)
                it.value.lastHash = hash
            }
        }
        receiver.updateShapes(shapesToUpdate)
    }

    @Synchronized
    fun updateVisibleShapes(newShapeList: List<IEditorShape<*>>) {
        val existingShapes = HashMap(_trackedShapes)
        val shapesToKeep = HashMap<String, IEditorShape<*>>()
        val shapesToRemove = ArrayList<IEditorShape<*>>()
        val shapesToAdd = ArrayList<IEditorShape<*>>()

        newShapeList.forEach {
            if (existingShapes.containsKey(it.id)) {
                shapesToKeep[it.id] = it
            } else {
                shapesToAdd.add(it)
            }
        }
        existingShapes.values.forEach {
            if (!shapesToKeep.contains(it.shape.id)) shapesToRemove.add(it.shape)
        }

        receiver.removeShapes(shapesToRemove)
        receiver.updateShapes(shapesToAdd)
    }

}