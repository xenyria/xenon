/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape

import net.xenyria.xenon.core.*
import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IEditorShape<PropertiesType : IEditorShapeProperties>(
    val type: ShapeType,
    properties: PropertiesType
) {

    val id: String
        get() = _id
    private lateinit var _id: String

    val group: String
        get() = _group
    private lateinit var _group: String

    var properties: PropertiesType = properties
        protected set

    private lateinit var _position: IVec3D
    val position: IVec3D
        get() = _position

    open val textDisplayOrigin: IVec3D
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