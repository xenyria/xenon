package net.xenyria.xenon.shape

import net.xenyria.xenon.server.IXenonClient

class ShapeNotificationReceiver(private val receiver: IXenonClient) {
    fun clearShapes() {
        //receiver.sendPluginMessage()
    }

    fun removeShapes(shapesToRemove: List<IEditorShape<*>>) {
    }

    fun updateShapes(shapesToAdd: ArrayList<IEditorShape<*>>) {

    }
}