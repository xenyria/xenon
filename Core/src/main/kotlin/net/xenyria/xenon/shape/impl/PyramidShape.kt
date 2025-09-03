/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape.impl

import net.xenyria.xenon.core.*
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.IEditorShapeProperties
import net.xenyria.xenon.shape.ShapeType
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class PyramidShapeProperties(
    baseCenter: IVec3D = ZERO,
    apex: IVec3D = ZERO,
    baseSize: Float = 1.0F,
    outlineColor: Color = Color.WHITE,
    visibleThroughWalls: Boolean = false
) : IEditorShapeProperties() {

    var baseCenter: IVec3D = baseCenter
        private set
    var apex: IVec3D = apex
        private set
    var baseSize: Float = baseSize
        private set
    var outlineColor: Color = outlineColor
        private set
    var visibleThroughWalls: Boolean = visibleThroughWalls
        private set

    override fun writeToStream(stream: DataOutputStream) {
        stream.writeVec3D(baseCenter)
        stream.writeVec3D(apex)
        stream.writeFloat(baseSize)
        stream.writeRGBA(outlineColor)
        stream.writeBoolean(visibleThroughWalls)
    }

    override fun readFromStream(stream: DataInputStream) {
        baseCenter = stream.readVec3D()
        apex = stream.readVec3D()
        baseSize = stream.readFloat()
        outlineColor = stream.readRGBA()
        visibleThroughWalls = stream.readBoolean()
    }

    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.putVector("baseCenter", baseCenter)
        json.putVector("apex", apex)
        json.put("baseSize", baseSize)
        json.putColor("outlineColor", outlineColor)
        json.put("visibleThroughWalls", visibleThroughWalls)
        return json
    }

}

class PyramidShape : IEditorShape<PyramidShapeProperties>(ShapeType.SPHERE, PyramidShapeProperties()) {

    override val textDisplayOrigin: IVec3D
        get() = properties.apex + OFFSET

    companion object {
        val OFFSET = Vec3D(0.0, 0.25, 0.0)
    }
}