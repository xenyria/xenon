package net.xenyria.xenon.shape

import net.openhft.hashing.LongHashFunction
import net.xenyria.xenon.core.readVarInt
import net.xenyria.xenon.core.readVec3F
import net.xenyria.xenon.core.writeVarInt
import net.xenyria.xenon.core.writeVec3F
import org.joml.Vector3dc
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IEditorShape<PropertiesType : IEditorShapeProperties>(
    val type: ShapeType,
    properties: PropertiesType
) {

    constructor(
        id: String,
        position: Vector3dc, type: ShapeType,
        properties: PropertiesType, textLines: List<String> = emptyList(),
        group: String = "",
    ) : this(type, properties) {
        this._id = id
        this._group = group
        this._position = position
        this.textLines = textLines.toList()
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

    var textLines: List<String> = emptyList()

    val hash: Long
        get() {
            val seed = id
            val output = ByteArrayOutputStream()
            val stream = DataOutputStream(output)
            serialize(stream)
            return LongHashFunction.murmur_3(seed.hashCode().toLong()).hashBytes(output.toByteArray())
        }

    fun deserialize(input: DataInputStream) {
        _id = input.readUTF()
        _position = input.readVec3F()
        _group = input.readUTF()

        val lineCount = input.readVarInt()
        val linesList = ArrayList<String>()
        repeat(lineCount) { linesList.add(input.readUTF()) }
        textLines = linesList
        properties.readFromStream(input)
    }

    fun serialize(output: DataOutputStream) {
        output.writeUTF(id)
        output.writeVec3F(position)
        output.writeUTF(group)

        output.writeVarInt(textLines.size)
        textLines.forEach { output.writeUTF(it) }
        properties.writeToStream(output)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IEditorShape<*>

        if (type != other.type) return false
        if (_id != other._id) return false
        if (_group != other._group) return false
        if (properties != other.properties) return false
        if (_position != other._position) return false
        if (textLines != other.textLines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + _id.hashCode()
        result = 31 * result + _group.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + _position.hashCode()
        result = 31 * result + textLines.hashCode()
        return result
    }

    override fun toString(): String {
        return "${this::class.java.simpleName}(id='$id', group='$group', properties=$properties, position=$position, textLines=$textLines)"
    }


}