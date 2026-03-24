package net.xenyria.xenon.forklift.util

import org.joml.Vector3d
import org.joml.Vector3dc

const val CATMULL_BASE: Double = 0.5

class Catmull private constructor(prev: Vector3dc, from: Vector3dc, to: Vector3dc, next: Vector3dc) {

    private val points: List<Vector3dc> = listOf(prev, from, to, next)

    /**
     * Returns the vector at the given index. If the index is out of bounds, the first or last vector is returned.
     */
    private fun get(index: Int): Vector3dc {
        if (index <= 0) return points[0]
        if (index >= (points.size - 1)) return points[points.size - 1]
        return points[index]
    }

    companion object {
        /**
         * Interpolates a single point
         */
        private fun q(t: Double, p0: Double, p1: Double, p2: Double, p3: Double): Double {
            return CATMULL_BASE * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * (t * t) + ((-p0 + 3 * p1 - 3
                    * p2 + p3)
                    * (t * t * t)))
        }

        /**
         * Interpolates four points to a list of points
         */
        fun interpolateSegment(
            amount: Int, includeTarget: Boolean,
            prev: Vector3dc, from: Vector3dc, to: Vector3dc, next: Vector3dc
        ): List<Vector3dc> {
            val catmull = Catmull(prev, from, to, next)
            val list = ArrayList<Vector3dc>(amount)

            val before = catmull.get(0)
            val start = catmull.get(1)
            val target = catmull.get(2)
            val end = catmull.get(3)

            val increment = 1.0 / amount.toDouble()
            for (index in 0..<amount) {
                val t = increment * index
                val x = q(t, before.x(), start.x(), target.x(), end.x())
                val y = q(t, before.y(), start.y(), target.y(), end.y())
                val z = q(t, before.z(), start.z(), target.z(), end.z())
                list.add(Vector3d(x, y, z))
            }
            if (includeTarget) list.add(target)
            return list.toList()
        }
    }
}
