/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package net.xenyria.xenon.core

import java.io.InputStream
import java.io.OutputStream

const val SEGMENT_BITS = 0x7F
const val CONTINUE_BIT = 0x80

fun OutputStream.writeVarInt(value: Int) {
    var value = value
    while (true) {
        if ((value and SEGMENT_BITS.inv()) == 0) {
            write(value)
            return
        }
        write(((value and SEGMENT_BITS) or CONTINUE_BIT))
        value = value ushr 7
    }
}

fun InputStream.readVarInt(): Int {
    var value = 0
    var position = 0
    var currentByte: Byte
    while (true) {
        currentByte = read().toByte()
        value = value or ((currentByte.toInt() and SEGMENT_BITS) shl position)
        if ((currentByte.toInt() and CONTINUE_BIT) == 0) break
        position += 7
        if (position >= 32) throw RuntimeException("VarInt is too big")
    }
    return value
}