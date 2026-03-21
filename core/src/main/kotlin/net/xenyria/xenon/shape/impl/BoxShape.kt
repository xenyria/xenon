package net.xenyria.xenon.shape.impl

import net.xenyria.xenon.core.*
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.ShapeType
import org.joml.Vector3d
import org.joml.Vector3dc
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class BoxShapeProperties(
    var dimensions: Vector3dc = ZERO,
    var boxColor: Color = Color.WHITE,
    var outlineColor: Color = Color(255, 255, 255, 64),
    var visibleThroughWalls: Boolean = true,
    var onlyRenderOutline: Boolean = false,
    var centerTextVertically: Boolean = true
) : net.xenyria.xenon.shape.IEditorShapeProperties() {

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeVec3F(dimensions)
        stream.writeRGB(boxColor)
        stream.writeRGB(outlineColor)
        stream.writeByteBitSet(centerTextVertically, onlyRenderOutline, centerTextVertically)
    }

    override fun readFromStream(stream: DataInputStream) {
        dimensions = stream.readVec3F()
        boxColor = stream.readRGB()
        outlineColor = stream.readRGB()
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

    override fun hashCode(): Int {
        return Objects.hash(
            dimensions, boxColor, outlineColor,
            visibleThroughWalls, centerTextVertically, onlyRenderOutline
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoxShapeProperties

        if (visibleThroughWalls != other.visibleThroughWalls) return false
        if (onlyRenderOutline != other.onlyRenderOutline) return false
        if (centerTextVertically != other.centerTextVertically) return false
        if (dimensions != other.dimensions) return false
        if (boxColor != other.boxColor) return false
        if (outlineColor != other.outlineColor) return false

        return true
    }
}

class BoxShape : IEditorShape<BoxShapeProperties> {

    constructor() : super(ShapeType.BOX, BoxShapeProperties())
    constructor(position: Vector3dc, properties: BoxShapeProperties) : super(position, ShapeType.BOX, properties)

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
        }

}
