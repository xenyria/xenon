package net.xenyria.xenon.core

import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import kotlin.math.abs

/**
 * Represents the result of a ray cast.
 */
data class Intersection(
    val hitPosition: Vector3d, // Vector where the ray hit something
    val blockPosition: Vector3i, // Position of the block that was hit
    val face: CubeFace, // The face of the block that was hit, only present if we hit a block.
    val distance: Double // Distance from the ray's origin to the hit position
)

private const val EPSILON = 1e-10 // small value to avoid division by zero

interface IIntersectable {
    val boundingBoxes: List<Box>
}

class TypedIntersection<Type : IIntersectable>(val data: Type, val intersection: Intersection)

object RayCast {

    /**
     * Performs a raycast on the given box. The raycast starts at the given start vector and goes in the given
     * direction. The ray-cast will stop if it hits the box or if it travels the given maximum distance.
     *
     * @return The result of the raycast, or null if no intersection was found.
     */
    fun intersection(box: Box, start: Vector3dc, direction: Vector3dc, maxDist: Double): Intersection? {
        // Ray start
        val startX: Double = start.x()
        val startY: Double = start.y()
        val startZ: Double = start.z()

        // Ray direction
        val dir = normalizeZeros(Vector3d(direction).normalize())

        if (!validateVector(dir) || maxDist.isNaN() || maxDist.isInfinite() || maxDist <= 0 || !validateVector(start)) {
            return null
        }

        var dirX: Double = dir.x
        var dirY: Double = dir.y
        var dirZ: Double = dir.z

        if (abs(dirX) == 0.0) dirX = EPSILON
        if (abs(dirY) == 0.0) dirY = EPSILON
        if (abs(dirZ) == 0.0) dirZ = EPSILON

        val divX = 1.0 / dirX
        val divY = 1.0 / dirY
        val divZ = 1.0 / dirZ

        var tMin: Double
        var tMax: Double
        var hitBlockFaceMin: CubeFace?
        var hitBlockFaceMax: CubeFace?

        // intersections with x planes:
        if (dirX >= 0.0) {
            tMin = (box.minX - startX) * divX
            tMax = (box.maxX - startX) * divX
            hitBlockFaceMin = CubeFace.WEST
            hitBlockFaceMax = CubeFace.EAST
        } else {
            tMin = (box.maxX - startX) * divX
            tMax = (box.minX - startX) * divX
            hitBlockFaceMin = CubeFace.EAST
            hitBlockFaceMax = CubeFace.WEST
        }

        // intersections with y planes:
        val tyMin: Double
        val tyMax: Double
        val hitBlockFaceYMin: CubeFace?
        val hitBlockFaceYMax: CubeFace?
        if (dirY >= 0.0) {
            tyMin = (box.minY - startY) * divY
            tyMax = (box.maxY - startY) * divY
            hitBlockFaceYMin = CubeFace.DOWN
            hitBlockFaceYMax = CubeFace.UP
        } else {
            tyMin = (box.maxY - startY) * divY
            tyMax = (box.minY - startY) * divY
            hitBlockFaceYMin = CubeFace.UP
            hitBlockFaceYMax = CubeFace.DOWN
        }

        if ((tMin > tyMax) || (tMax < tyMin)) return null
        if (tyMin > tMin) {
            tMin = tyMin
            hitBlockFaceMin = hitBlockFaceYMin
        }
        if (tyMax < tMax) {
            tMax = tyMax
            hitBlockFaceMax = hitBlockFaceYMax
        }

        // intersections with z planes:
        val tzMin: Double
        val tzMax: Double
        val hitBlockFaceZMin: CubeFace?
        val hitBlockFaceZMax: CubeFace?
        if (dirZ >= 0.0) {
            tzMin = (box.minZ - startZ) * divZ
            tzMax = (box.maxZ - startZ) * divZ
            hitBlockFaceZMin = CubeFace.NORTH
            hitBlockFaceZMax = CubeFace.SOUTH
        } else {
            tzMin = (box.maxZ - startZ) * divZ
            tzMax = (box.minZ - startZ) * divZ
            hitBlockFaceZMin = CubeFace.SOUTH
            hitBlockFaceZMax = CubeFace.NORTH
        }
        if ((tMin > tzMax) || (tMax < tzMin)) return null
        if (tzMin > tMin) {
            tMin = tzMin
            hitBlockFaceMin = hitBlockFaceZMin
        }
        if (tzMax < tMax) {
            tMax = tzMax
            hitBlockFaceMax = hitBlockFaceZMax
        }

        // Discard intersections behind the start & those that are too far away:
        if (tMax < 0.0 || tMin > maxDist) return null

        // find the closest intersection:
        val time: Double
        val hitFace: CubeFace?
        if (tMin < 0.0) {
            time = tMax
            hitFace = hitBlockFaceMax
        } else {
            time = tMin
            hitFace = hitBlockFaceMin
        }

        val hitPosition = Vector3d(
            (dir.x * time) + start.x(),
            (dir.y * time) + start.y(),
            (dir.z * time) + start.z()
        )
        if (!validateVector(hitPosition)) return null
    
        val distance = start.distance(hitPosition)
        if (distance.isNaN() || distance.isInfinite()) return null

        return Intersection(hitPosition, toBlockPosition(hitPosition, hitFace), hitFace, distance)
    }

    /**
     * Attempts to find the nearest intersection of the given items.
     */
    fun <Type : IIntersectable> findNearestIntersection(items: List<Type>, start: Vector3dc, direction: Vector3dc, maxDist: Double): TypedIntersection<Type>? {
        if (items.isEmpty()) return null
        val intersectionResults = ArrayList<TypedIntersection<Type>>()
        for (item in items) {
            var lowestDistance: Double = Double.MAX_VALUE
            var bestIntersection: Intersection? = null

            for (box in item.boundingBoxes) {
                val result = intersection(box, start, direction, maxDist) ?: continue
                if (result.distance < lowestDistance) {
                    lowestDistance = result.distance
                    bestIntersection = result
                }
            }

            if (bestIntersection != null) {
                intersectionResults.add(TypedIntersection(item, bestIntersection))
                break
            }
        }
        return intersectionResults.minByOrNull { it.intersection.distance }
    }

    private fun toBlockPosition(hitPosition: Vector3d, hitFace: CubeFace): Vector3i {
        val result = Vector3d(hitPosition).add(hitFace.normal.mul(-0.001))
        return Vector3i(
            toBlockCoordinate(result.x),
            toBlockCoordinate(result.y),
            toBlockCoordinate(result.z)
        )
    }
}