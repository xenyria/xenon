package net.xenyria.xenon.shape.impl


import net.xenyria.xenon.core.*
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.IEditorShapeProperties
import net.xenyria.xenon.shape.ShapeType
import org.joml.Vector3dc
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class SphereShapeProperties(
    var radius: Float = 1.0F,
    var color: Color = Color.WHITE,
    var visibleThroughWalls: Boolean = false,
    var isOutline: Boolean = false,
    var stacks: Int? = null,
    var slices: Int? = null
) : IEditorShapeProperties() {

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeFloat(radius)
        stream.writeRGBA(color)
        stream.writeBoolean(visibleThroughWalls)
        stream.writeBoolean(isOutline)
        stream.writeOptional(stacks) { stream.writeVarInt(it) }
        stream.writeOptional(slices) { stream.writeVarInt(it) }
    }

    override fun readFromStream(stream: DataInputStream) {
        radius = stream.readFloat()
        color = stream.readRGBA()
        visibleThroughWalls = stream.readBoolean()
        isOutline = stream.readBoolean()
        stacks = stream.readOptional { it.readVarInt() }
        slices = stream.readOptional { it.readVarInt() }
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("radius", radius)
        json.putColor("color", color)
        json.put("visibleThroughWalls", visibleThroughWalls)
        return json
    }
}

class SphereShape : IEditorShape<SphereShapeProperties> {
    constructor() : super(ShapeType.SPHERE, SphereShapeProperties())
    constructor(
        id: String,
        position: Vector3dc,
        properties: SphereShapeProperties,
        textLines: List<String> = emptyList(),
        group: String = "",
    ) : super(id, position, ShapeType.SPHERE, properties, textLines, group)
}