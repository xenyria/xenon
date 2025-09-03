/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape.impl

import net.xenyria.xenon.core.*
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.shape.ShapeType
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class BoxShapeProperties(
    dimensions: IVec3D = ZERO,
    boxColor: Color = Color.WHITE,
    outlineColor: Color = Color(255, 255, 255, 64),
    visibleThroughWalls: Boolean = true,
    onlyRenderOutline: Boolean = true,
    centerTextVertically: Boolean = true
) : net.xenyria.xenon.shape.IEditorShapeProperties() {

    var dimensions: IVec3D = dimensions
        private set
    var boxColor: Color = boxColor
        private set
    var outlineColor: Color = outlineColor
        private set
    var visibleThroughWalls: Boolean = visibleThroughWalls
        private set
    var onlyRenderOutline: Boolean = onlyRenderOutline
        private set
    var centerTextVertically: Boolean = centerTextVertically
        private set

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

class BoxShape : IEditorShape<BoxShapeProperties>(ShapeType.BOX, BoxShapeProperties()) {
    override val textDisplayOrigin: IVec3D
        get() {
            if (properties.centerTextVertically)
                return position + (properties.dimensions / 2.0)
            return position + (properties.dimensions / 2.0) +
                    Vec3D(0.0, properties.dimensions.y / 2.0, 0.0)
        }

}
