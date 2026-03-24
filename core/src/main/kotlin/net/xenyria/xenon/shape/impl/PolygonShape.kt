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


class PolygonShapeProperties(
    var color: Color = Color.WHITE,
    var points: List<Vector3dc> = emptyList(),
    var visibleThroughWalls: Boolean = false
) : IEditorShapeProperties() {

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeBoolean(visibleThroughWalls)
        stream.writeRGBA(color)
        stream.writeVarInt(points.size)
        points.forEach { stream.writeVec3F(it) }
    }

    override fun readFromStream(stream: DataInputStream) {
        visibleThroughWalls = stream.readBoolean()
        color = stream.readRGBA()
        val pointCount = stream.readVarInt()
        val loadedPoints = ArrayList<Vector3dc>(pointCount)
        repeat(pointCount) {
            loadedPoints.add(stream.readVec3F())
        }
        this.points = loadedPoints
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.putColor("color", color)
        val points = json.getJSONArray("points")
        for (vec in this.points)
            points.putVector(vec)
        return json
    }
}

class PolygonShape : IEditorShape<PolygonShapeProperties> {
    fun getCullingBox(): Box {
        if (properties.points.isEmpty()) return makeBoxFromPoints(listOf(position))
        return makeBoxFromPoints(properties.points)
    }

    constructor() : super(ShapeType.POLYGON, PolygonShapeProperties())
    constructor(
        id: String,
        position: Vector3dc,
        properties: PolygonShapeProperties,
        textLines: List<String> = emptyList(),
        group: String = "",
    ) : super(id, position, ShapeType.POLYGON, properties, textLines, group)

    override val textDisplayOrigin: Vector3dc
        get() {
            if (properties.points.isEmpty()) return position
            val center = Vector3d()
            for (point in properties.points) {
                center.add(point)
            }
            center.div(properties.points.size.toDouble())
            return center
        }

}