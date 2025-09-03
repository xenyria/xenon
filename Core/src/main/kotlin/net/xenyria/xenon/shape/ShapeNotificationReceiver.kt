/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

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