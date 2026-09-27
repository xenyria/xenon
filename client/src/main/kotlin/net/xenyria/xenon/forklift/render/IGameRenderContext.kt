package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive

/**
 * Interface providing contextual information and operations for doing render operations.
 */
interface IGameRenderContext {

    /**
     * Tests if a given box is within the camera's frustum.
     */
    fun isInCameraFrustum(box: Box): Boolean

    /**
     * Instructs the renderer to draw a list of primitives in this frame.
     */
    fun drawPrimitives(primitives: List<IRenderPrimitive>, visibleThroughWalls: Boolean)
}