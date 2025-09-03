/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape.impl

import net.xenyria.xenon.core.*
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class PathShapeProperties : net.xenyria.xenon.shape.IEditorShapeProperties() {

    lateinit var color: Color
        private set

    lateinit var waypoints: List<IVec3D>
        private set

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeRGB(color)
        stream.writeInt(waypoints.size)
        waypoints.forEach { stream.writeVec3F(it) }
    }

    override fun readFromStream(stream: DataInputStream) {
        color = stream.readRGB()
        val waypointCount = stream.readInt()
        val loadedWaypoints = ArrayList<IVec3D>(waypointCount)
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

class PathShape : net.xenyria.xenon.shape.IEditorShape<PathShapeProperties>(_root_ide_package_.net.xenyria.xenon.shape.ShapeType.PATH, PathShapeProperties()) {

    override val textDisplayOrigin: IVec3D
        get() {
            return properties.waypoints.firstOrNull() ?: position
        }

}