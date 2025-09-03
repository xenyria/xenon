/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.shape

import net.openhft.hashing.LongHashFunction
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

abstract class IEditorShapeProperties {

    constructor()

    /**
     * Deserialize the object from a stream.
     */
    constructor(stream: DataInputStream) {
        readFromStream(stream)
    }

    abstract fun writeToStream(stream: DataOutputStream)

    abstract fun readFromStream(stream: DataInputStream)

    fun createHash(seed: String): Long {
        val output = ByteArrayOutputStream()
        val stream = DataOutputStream(output)
        writeToStream(stream)
        return LongHashFunction.murmur_3(seed.hashCode().toLong()).hashBytes(output.toByteArray())
    }

    /**
     * Converts the state of this object to a JSON object.
     * This is used to transmit debug shape state to Landscape's 3D view in Spotlight.
     */
    abstract fun toJSON(): JSONObject
}
