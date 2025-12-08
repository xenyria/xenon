package net.xenyria.xenon.shape.impl


import net.xenyria.xenon.core.putColor
import net.xenyria.xenon.core.readRGBA
import net.xenyria.xenon.core.writeRGBA
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.ShapeType
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class SphereShapeProperties(
    radius: Float = 1.0F,
    lineColor: Color = Color.WHITE,
    visibleThroughWalls: Boolean = false
) : net.xenyria.xenon.shape.IEditorShapeProperties() {

    var radius: Float = radius
        private set

    var lineColor: Color = lineColor
        private set

    var visibleThroughWalls: Boolean = visibleThroughWalls
        private set

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeFloat(radius)
        stream.writeRGBA(lineColor)
        stream.writeBoolean(visibleThroughWalls)
    }

    override fun readFromStream(stream: DataInputStream) {
        radius = stream.readFloat()
        lineColor = stream.readRGBA()
        visibleThroughWalls = stream.readBoolean()
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("radius", radius)
        json.putColor("lineColor", lineColor)
        json.put("visibleThroughWalls", visibleThroughWalls)
        return json
    }
}

class SphereShape : IEditorShape<SphereShapeProperties>(ShapeType.SPHERE, SphereShapeProperties())