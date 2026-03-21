package net.xenyria.xenon.shape.impl

import net.xenyria.xenon.core.*
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.ShapeType
import org.joml.Vector3dc
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class PathShapeProperties : net.xenyria.xenon.shape.IEditorShapeProperties() {

    lateinit var color: Color
        private set

    lateinit var waypoints: List<Vector3dc>
        private set

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeRGB(color)
        stream.writeInt(waypoints.size)
        waypoints.forEach { stream.writeVec3F(it) }
    }

    override fun readFromStream(stream: DataInputStream) {
        color = stream.readRGB()
        val waypointCount = stream.readInt()
        val loadedWaypoints = ArrayList<Vector3dc>(waypointCount)
        repeat(waypointCount) {
            loadedWaypoints.add(stream.readVec3F())
        }
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.putColor("color", color)
        val points = json.getJSONArray("waypoints")
        for (vec in this.waypoints)
            points.putVector(vec)
        return json
    }

}

class PathShape : IEditorShape<PathShapeProperties>(ShapeType.PATH, PathShapeProperties()) {

    override val textDisplayOrigin: Vector3dc
        get() {
            return properties.waypoints.firstOrNull() ?: position
        }

}