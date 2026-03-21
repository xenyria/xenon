package net.xenyria.xenon.shape

import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.readVec3D
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.core.writeVec3D
import org.joml.Vector3dc
import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IEditorShape<PropertiesType : IEditorShapeProperties>(
    val type: ShapeType,
    properties: PropertiesType
) {

    constructor(position: Vector3dc, type: ShapeType, properties: PropertiesType) : this(type, properties) {
        this._position = position
    }

    val id: String
        get() = _id
    private lateinit var _id: String

    val group: String
        get() = _group
    private lateinit var _group: String

    var properties: PropertiesType = properties
        protected set

    private lateinit var _position: Vector3dc
    val position: Vector3dc
        get() = _position

    fun updatePosition(position: Vector3dc) {
        _position = position
    }

    open val textDisplayOrigin: Vector3dc
        get() = _position

    private var _lines: List<String> = emptyList()

    val hash: Long
        get() = properties.createHash(id)

    fun deserialize(input: DataInputStream) {
        _id = input.readUTF()
        _position = input.readVec3D()
        _group = input.readUTF()

        val lineCount = input.readVarInt()
        val linesList = ArrayList<String>()
        repeat(lineCount) { linesList.add(input.readUTF()) }
        _lines = linesList
        properties.readFromStream(input)
    }

    fun serialize(output: DataOutputStream) {
        output.writeUTF(id)
        output.writeVec3D(position)
        output.writeUTF(group)
        output.writeVarInt(_lines.size)
        _lines.forEach { output.writeUTF(it) }
        properties.writeToStream(output)
    }

}