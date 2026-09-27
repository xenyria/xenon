package net.xenyria.xenon.forklift.render.shape

import net.xenyria.xenon.forklift.render.IGameRenderContext
import net.xenyria.xenon.forklift.render.IShapeRenderer
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.forklift.render.primitive.LinePrimitive
import net.xenyria.xenon.shape.impl.PolygonShape
import net.xenyria.xenon.shape.impl.PolygonShapeProperties
import org.joml.Vector3d

const val POLYGON_LINE_THICKNESS = 4.0F

/**
 * Renderer implementation for polygon shape outlines.
 */
object PolygonShapeRenderer : IShapeRenderer<PolygonShape> {

    private fun getPolygonVerticalSurfaceVectors(shape: PolygonShape, y: Double): List<Vector3d> {
        val surfaceVectors = ArrayList<Vector3d>()
        for (point in shape.properties.points) {
            surfaceVectors.add(Vector3d(point.x(), y, point.z()))
        }
        return surfaceVectors
    }

    private fun drawPolygonSurface(
        renderer: IGameRenderContext,
        properties: PolygonShapeProperties,
        surfaceVectors: List<Vector3d>,
        visibleThroughWalls: Boolean
    ) {
        val linesToDraw = ArrayList<LinePrimitive>()
        for (i in surfaceVectors.indices) {
            val point = surfaceVectors[i]
            val nextPoint = surfaceVectors[(i + 1) % surfaceVectors.size]
            linesToDraw.add(LinePrimitive(Line(point, nextPoint, POLYGON_LINE_THICKNESS, properties.color)))
        }
        renderer.drawPrimitives(linesToDraw, visibleThroughWalls)
    }

    private fun drawTopAndBottomFaces(
        access: IGameRenderContext,
        polygonShape: PolygonShape,
        maxY: Double,
        minY: Double
    ) {
        val topSurfaceVectors = getPolygonVerticalSurfaceVectors(polygonShape, maxY)
        val bottomSurfaceVectors = getPolygonVerticalSurfaceVectors(polygonShape, minY)

        drawPolygonSurface(
            access,
            polygonShape.properties,
            topSurfaceVectors,
            polygonShape.properties.visibleThroughWalls
        )
        drawPolygonSurface(
            access,
            polygonShape.properties,
            bottomSurfaceVectors,
            polygonShape.properties.visibleThroughWalls
        )
    }

    override fun extract(renderer: IGameRenderContext, shape: PolygonShape): Boolean {
        val box = shape.getCullingBox().grow(0.25, 0.25, 0.25)
        if (!renderer.isInCameraFrustum(box)) return false

        val minY = box.minY
        val maxY = box.maxY
        drawTopAndBottomFaces(renderer, shape, maxY, minY)

        val primitives = ArrayList<IRenderPrimitive>()
        for (position in shape.properties.points) {
            primitives.add(
                LinePrimitive(
                    Line(
                        Vector3d(position.x(), minY, position.z()),
                        Vector3d(position.x(), maxY, position.z()),
                        POLYGON_LINE_THICKNESS,
                        shape.properties.color
                    ),
                )
            )
        }
        renderer.drawPrimitives(primitives, shape.properties.visibleThroughWalls)
        return true
    }

}