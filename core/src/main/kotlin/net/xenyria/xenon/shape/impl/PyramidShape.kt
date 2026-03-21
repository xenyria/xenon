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
    baseCenter: Vector3dc = ZERO,
    apex: Vector3dc = ZERO,
    baseSize: Float = 1.0F,
    outlineColor: Color = Color.WHITE,
    visibleThroughWalls: Boolean = false
) : IEditorShapeProperties() {

    var baseCenter: Vector3dc = baseCenter
        private set
    var apex: Vector3dc = apex
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

    override val textDisplayOrigin: Vector3dc get() = Vector3d(properties.apex).add(OFFSET)

    companion object {
        val OFFSET: Vector3dc = Vector3d(0.0, 0.25, 0.0)
    }
}