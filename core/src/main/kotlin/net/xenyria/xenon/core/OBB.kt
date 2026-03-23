/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */
package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.abs

/**
 * Represents an oriented bounding box (OBB) in 3D space. This class is immutable.
 * Heavily "inspired" by https://gamedev.stackexchange.com/a/160547
 */
class OBB(position: Vector3dc, sizeX: Double, sizeY: Double, sizeZ: Double) {

    // Center of the bounding box
    private val center: Vector3dc

    // Each axis of the bounding box
    private var axisX: Vector3dc = DEFAULT_AXIS_X
    private var axisY: Vector3dc = DEFAULT_AXIS_Y
    private var axisZ: Vector3dc = DEFAULT_AXIS_Z

    var halfSizeX: Double
    var halfSizeY: Double
    var halfSizeZ: Double

    /**
     * Constructor
     */
    init {
        this.center = Vector3d(position)
        this.halfSizeX = sizeX / CENTER_DIVIDER
        this.halfSizeY = sizeY / CENTER_DIVIDER
        this.halfSizeZ = sizeZ / CENTER_DIVIDER
    }

    /**
     * Returns a point relative to the center of the bounding box, based on the rotation of the box.
     */
    fun point(x: Double, y: Double, z: Double): Vector3d {
        return Vector3d(center)
            .add(Vector3d(axisX).mul(x))
            .add(Vector3d(axisY).mul(y))
            .add(Vector3d(axisZ).mul(z))
    }

    fun overlaps(box2: OBB): Boolean {
        return overlaps(this, box2)
    }

    fun overlaps(boundingBox: Box): Boolean {
        val widthX = boundingBox.sizeX
        val widthY = boundingBox.sizeY
        val widthZ = boundingBox.sizeZ
        return overlaps(OBB(boundingBox.center, widthX, widthY, widthZ))
    }

    fun setOrientation(x: Vector3dc, y: Vector3dc, z: Vector3dc) {
        this.axisX = Vector3d(x)
        this.axisY = Vector3d(y)
        this.axisZ = Vector3d(z)
    }

    fun intersection(
        rayOrigin: Vector3dc,
        rayDirection: Vector3dc
    ): Vector3dc? {
        var tMin = Double.NEGATIVE_INFINITY
        var tMax = Double.POSITIVE_INFINITY

        val p = Vector3d(center).sub(rayOrigin)

        val obbBasis = arrayOf(axisX, axisY, axisZ)
        val obbHalfSize = doubleArrayOf(halfSizeX, halfSizeY, halfSizeZ)

        for (i in 0..2) {
            val e: Double = dotProduct(obbBasis[i], p)
            val f: Double = dotProduct(rayDirection, obbBasis[i])

            if (abs(f) > 0.001) {
                var t1 = (e + obbHalfSize[i]) / f
                var t2 = (e - obbHalfSize[i]) / f

                if (t1 > t2) {
                    val w = t1
                    t1 = t2
                    t2 = w
                }

                if (t2 < tMax) tMax = t2
                if (t1 > tMin) tMin = t1
                if (tMax < tMin) return null
            } else if (-e - obbHalfSize[i] > 0 || -e + obbHalfSize[i] < 0) {
                return null
            }
        }

        return if (tMin > 0) {
            Vector3d(rayOrigin).add(Vector3d(rayDirection).mul(tMin))
        } else {
            Vector3d(rayOrigin).add(Vector3d(rayDirection).mul(tMax))
        }
    }

    companion object {
        var DEFAULT_AXIS_X = Axis.X.positive
        var DEFAULT_AXIS_Y = Axis.Y.positive
        var DEFAULT_AXIS_Z = Axis.Z.positive

        const val CENTER_DIVIDER: Double = 2.0

        private fun crossProduct(
            self: Vector3dc,
            rhs: Vector3dc
        ): Vector3dc {
            return Vector3d(
                self.y() * rhs.z() - self.z() * rhs.y(),
                self.z() * rhs.x() - self.x() * rhs.z(),
                self.x() * rhs.y() - self.y() * rhs.x()
            )
        }

        private fun dotProduct(
            self: Vector3dc,
            rhs: Vector3dc
        ): Double {
            return (self.x() * rhs.x() + self.y() * rhs.y() + self.z() * rhs.z())
        }

        fun overlaps(box1: OBB, box2: OBB): Boolean {
            val rpos = Vector3d(box2.center).sub(box1.center)
            return !(getSeparatingPlane(rpos, box1.axisX, box1, box2) ||
                    getSeparatingPlane(rpos, box1.axisY, box1, box2) ||
                    getSeparatingPlane(rpos, box1.axisZ, box1, box2) ||
                    getSeparatingPlane(rpos, box2.axisX, box1, box2) ||
                    getSeparatingPlane(rpos, box2.axisY, box1, box2) ||
                    getSeparatingPlane(rpos, box2.axisZ, box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisX, box2.axisX), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisX, box2.axisY), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisX, box2.axisZ), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisY, box2.axisX), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisY, box2.axisY), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisY, box2.axisZ), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisZ, box2.axisX), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisZ, box2.axisY), box1, box2) ||
                    getSeparatingPlane(rpos, crossProduct(box1.axisZ, box2.axisZ), box1, box2))
        }

        private fun getSeparatingPlane(rpos: Vector3dc, plane: Vector3dc, box1: OBB, box2: OBB): Boolean {
            return (abs(dotProduct(rpos, plane)) > (abs(dotProduct((Vector3d(box1.axisX).mul(box1.halfSizeX)), plane)) + abs(
                dotProduct(
                    (Vector3d(box1.axisY).mul(box1.halfSizeY)), plane
                )
            ) + abs(
                dotProduct((Vector3d(box1.axisZ).mul(box1.halfSizeZ)), plane)
            ) + abs(dotProduct((Vector3d(box2.axisX).mul(box2.halfSizeX)), plane)) + abs(
                dotProduct(
                    (Vector3d(box2.axisY)
                        .mul(box2.halfSizeY)), plane
                )
            ) + abs(
                dotProduct((Vector3d(box2.axisZ).mul(box2.halfSizeZ)), plane)
            )))
        }
    }
}
