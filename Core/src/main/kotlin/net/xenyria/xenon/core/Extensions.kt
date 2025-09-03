/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.core

import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

fun DataInputStream.readVec3F(): IVec3D {
    return Vec3D(readFloat().toDouble(), readFloat().toDouble(), readFloat().toDouble())
}

fun DataInputStream.readVec3D(): IVec3D {
    return Vec3D(readDouble(), readDouble(), readDouble())
}

fun DataInputStream.readRGB(): Color {
    return Color(readByte().toInt(), readByte().toInt(), readByte().toInt())
}

fun DataOutputStream.writeRGB(color: Color) {
    writeByte(color.red)
    writeByte(color.green)
    writeByte(color.blue)
}

fun DataInputStream.readRGBA(): Color {
    return Color(readInt())
}

fun DataOutputStream.writeRGBA(color: Color) {
    writeInt(color.rgb)
}

fun DataOutputStream.writeVec3F(vec3D: IVec3D) {
    writeFloat(vec3D.x.toFloat())
    writeFloat(vec3D.y.toFloat())
    writeFloat(vec3D.z.toFloat())
}

fun DataOutputStream.writeVec3D(vec3D: IVec3D) {
    writeDouble(vec3D.x)
    writeDouble(vec3D.y)
    writeDouble(vec3D.z)
}

fun DataInputStream.readUUID(): UUID {
    return UUID(readLong(), readLong())
}

fun DataOutputStream.writeUUID(uuid: UUID) {
    writeLong(uuid.mostSignificantBits)
    writeLong(uuid.leastSignificantBits)
}

inline fun <reified Type> DataOutputStream.writeList(data: List<Type>, writer: (Type) -> Unit) {
    writeInt(data.size)
    data.forEach { writer(it) }
}

inline fun <reified Type> DataInputStream.readList(reader: (DataInputStream) -> Type): List<Type> {
    val size = readInt()
    val list = ArrayList<Type>(size)
    repeat(size) {
        list.add(reader(this))
    }
    return list
}

inline fun <reified Type> DataInputStream.readSet(reader: (DataInputStream) -> Type): Set<Type> {
    val amount = readInt()
    val set = HashSet<Type>()
    repeat(amount) { set.add(reader(this)) }
    return set
}

inline fun <reified Type> DataOutputStream.writeSet(set: Set<Type>, writer: (Type) -> Unit): Set<Type> {
    writeInt(set.size)
    for (type in set) {
        writer(type)
    }
    return set
}

inline fun <reified Type> DataOutputStream.writeOptional(data: Type?, writer: (Type) -> Unit) {
    writeBoolean(data != null)
    if (data == null) return
    writer(data)
}

inline fun <reified Type> DataInputStream.readOptional(reader: (DataInputStream) -> Type): Type? {
    if (!readBoolean()) return null
    return reader(this)
}

fun vecToJsonArray(value: IVec3D): JSONArray {
    val array = JSONArray()
    array.put(value.x)
    array.put(value.y)
    array.put(value.z)
    return array
}

fun JSONObject.putVector(key: String, value: IVec3D): JSONObject {
    put(key, vecToJsonArray(value))
    return this
}

fun JSONArray.putVector(vec: IVec3D): JSONArray {
    put(vecToJsonArray(vec))
    return this
}

fun JSONObject.getVector(key: String): IVec3D? {
    if (!has(key)) return null
    val array = getJSONArray(key) ?: return null
    return Vec3D(array.getDouble(0), array.getDouble(1), array.getDouble(2))
}

fun JSONObject.putColor(name: String, color: Color): JSONObject {
    val obj = JSONObject()
    obj.put("r", color.red)
    obj.put("g", color.green)
    obj.put("b", color.blue)
    obj.put("a", color.alpha)
    put(name, obj)
    return this
}

fun JSONObject.getColor(name: String): Color {
    val obj = getJSONObject(name) ?: return Color.WHITE
    return Color(
        obj.getInt("r"),
        obj.getInt("g"),
        obj.getInt("b"),
        obj.getInt("a")
    )
}

fun DataOutputStream.writeByteBitSet(vararg varargs: Boolean) {
    var flags = 0
    var index = 0
    for (bit in varargs) {
        if (bit) {
            flags = flags or 1 shl index
        }
        index++
    }
    writeByte(flags)
}

fun DataInputStream.readByteBitSet(): BitSet {
    return BitSet.valueOf(byteArrayOf(readByte()))
}


