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

data class BoxShapeProperties(
    var dimensions: Vector3dc = Vector3d(1.0),
    var boxColor: Color = Color.WHITE,
    var outlineColor: Color = Color(192, 192, 192, 255),
    var visibleThroughWalls: Boolean = true,
    var onlyRenderOutline: Boolean = false,
    var centerTextVertically: Boolean = true,
) : IEditorShapeProperties() {

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeVec3F(dimensions)
        stream.writeRGBA(boxColor)
        stream.writeRGBA(outlineColor)
        stream.writeByteBitSet(centerTextVertically, onlyRenderOutline, centerTextVertically)
    }

    override fun readFromStream(stream: DataInputStream) {
        dimensions = stream.readVec3F()
        boxColor = stream.readRGBA()
        outlineColor = stream.readRGBA()

        val bitSet = stream.readByteBitSet()
        centerTextVertically = bitSet.get(0)
        onlyRenderOutline = bitSet.get(1)
        centerTextVertically = bitSet.get(2)
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.putVector("dimensions", dimensions)
        json.putColor("boxColor", boxColor)
        json.putColor("outlineColor", outlineColor)
        json.put("visibleThroughWalls", visibleThroughWalls)
        json.put("centerTextVertically", centerTextVertically)
        json.put("onlyOutline", onlyRenderOutline)
        return json
    }
}

class BoxShape : IEditorShape<BoxShapeProperties> {

    constructor() : super(ShapeType.BOX, BoxShapeProperties())
    constructor(
        id: String,
        position: Vector3dc,
        properties: BoxShapeProperties,
        textLines: List<String> = emptyList(),
        group: String = "",
    ) : super(id, position, ShapeType.BOX, properties, textLines, group)

    val box: Box
        get() = Box(
            position.x(), position.y(), position.z(),
            position.x() + properties.dimensions.x(),
            position.y() + properties.dimensions.y(),
            position.z() + properties.dimensions.z()
        )

    override val textDisplayOrigin: Vector3dc
        get() {
            if (properties.centerTextVertically)
                return Vector3d(position).add((Vector3d(properties.dimensions).div(2.0)))
            return Vector3d(position).add(Vector3d(properties.dimensions).div(2.0))
                .add(Vector3d(0.0, properties.dimensions.y() / 2.0, 0.0))
                .add(Vector3d(0.0, 0.5, 0.0))
        }

}
