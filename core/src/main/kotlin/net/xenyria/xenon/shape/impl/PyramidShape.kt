package net.xenyria.xenon.shape.impl

import net.xenyria.xenon.core.*
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.IEditorShapeProperties
import net.xenyria.xenon.shape.ShapeType
import org.joml.Vector3d
import org.joml.Vector3dc
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class PyramidShapeProperties(
    apex: Vector3dc = ZERO,
    baseSize: Float = 1.0F,
    outlineColor: Color = Color.WHITE,
    visibleThroughWalls: Boolean = false
) : IEditorShapeProperties() {

    var apex: Vector3dc = apex
        private set
    var baseSize: Float = baseSize
        private set
    var outlineColor: Color = outlineColor
        private set
    var visibleThroughWalls: Boolean = visibleThroughWalls
        private set

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeVec3D(apex)
        stream.writeFloat(baseSize)
        stream.writeRGBA(outlineColor)
        stream.writeBoolean(visibleThroughWalls)
    }

    override fun readFromStream(stream: DataInputStream) {
        apex = stream.readVec3D()
        baseSize = stream.readFloat()
        outlineColor = stream.readRGBA()
        visibleThroughWalls = stream.readBoolean()
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.putVector("apex", apex)
        json.put("baseSize", baseSize)
        json.putColor("outlineColor", outlineColor)
        json.put("visibleThroughWalls", visibleThroughWalls)
        return json
    }

}

class PyramidShape : IEditorShape<PyramidShapeProperties> {

    constructor() : super(ShapeType.PYRAMID, PyramidShapeProperties())
    constructor(
        id: String,
        position: Vector3dc,
        properties: PyramidShapeProperties,
        textLines: List<String> = emptyList(),
        group: String = "",
    ) : super(id, position, ShapeType.PYRAMID, properties, textLines, group)

    override val textDisplayOrigin: Vector3dc
        get() {
            return Vector3d(properties.apex).add(OFFSET)
        }

    companion object {
        val OFFSET: Vector3dc = Vector3d(0.0, 0.5, 0.0)
    }
}