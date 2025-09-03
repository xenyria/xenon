/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape

import net.xenyria.xenon.shape.impl.BoxShape
import net.xenyria.xenon.shape.impl.PathShape
import net.xenyria.xenon.shape.impl.PyramidShape
import net.xenyria.xenon.shape.impl.SphereShape
import java.io.DataInputStream

enum class ShapeType {

    BOX {
        override fun parseShape(data: DataInputStream): IEditorShape<*> {
            val shape = BoxShape()
            shape.deserialize(data)
            return shape
        }
    },
    PATH {
        override fun parseShape(data: DataInputStream): IEditorShape<*> {
            val shape = PathShape()
            shape.deserialize(data)
            return shape
        }
    },
    SPHERE {
        override fun parseShape(data: DataInputStream): IEditorShape<*> {
            val shape = SphereShape()
            shape.deserialize(data)
            return shape
        }
    },
    PYRAMID {
        override fun parseShape(data: DataInputStream): IEditorShape<*> {
            val shape = PyramidShape()
            shape.deserialize(data)
            return shape
        }
    };

    abstract fun parseShape(data: DataInputStream): IEditorShape<*>

}