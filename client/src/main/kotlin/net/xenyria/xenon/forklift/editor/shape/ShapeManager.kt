package net.xenyria.xenon.forklift.editor.shape

import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.shape.IEditorShape

class ShapeManager(val client: IGameClient) {

    private val _shapes = ArrayList<IEditorShape<*>>()

    @Synchronized
    fun reset() {
        _shapes.clear()
        client.renderShapes(_shapes)
    }

    @Synchronized
    fun updateShapes(newShapes: List<IEditorShape<*>>) {
        val newShapeMap = newShapes.associateBy { it.id }
        val newList = ArrayList<IEditorShape<*>>(_shapes.size)

        for (shape in _shapes) {
            val updatedShape = newShapeMap[shape.id]
            if (updatedShape != null) {
                newList.add(updatedShape)
            } else {
                newList.add(shape)
            }
        }

        _shapes.clear()
        _shapes.addAll(newList)
        client.renderShapes(_shapes)
    }

    @Synchronized
    fun removeShapes(shapeIds: Set<String>) {
        _shapes.removeIf { it.id in shapeIds }
        client.renderShapes(_shapes)
    }


}