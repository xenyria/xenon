package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive

interface IGameRenderer {
    fun isInCameraFrustum(box: Box): Boolean

    fun drawPrimitives(primitives: List<IRenderPrimitive>, visibleThroughWalls: Boolean)
}