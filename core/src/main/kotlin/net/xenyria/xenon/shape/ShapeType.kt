package net.xenyria.xenon.shape

import net.xenyria.xenon.shape.impl.*
import java.io.DataInputStream

enum class ShapeType {

    BOX {
        override fun createShape(): IEditorShape<*> {
            return BoxShape()
        }
    },
    PATH {
        override fun createShape(): IEditorShape<*> {
            return PathShape()
        }
    },
    SPHERE {
        override fun createShape(): IEditorShape<*> {
            return SphereShape()
        }
    },
    PYRAMID {
        override fun createShape(): IEditorShape<*> {
            return PyramidShape()
        }
    },
    POLYGON {
        override fun createShape(): IEditorShape<*> {
            return PolygonShape()
        }
    };

    abstract fun createShape(): IEditorShape<*>

    fun parseShape(stream: DataInputStream): IEditorShape<*> {
        val shape = createShape()
        shape.deserialize(stream)
        return shape
    }


}