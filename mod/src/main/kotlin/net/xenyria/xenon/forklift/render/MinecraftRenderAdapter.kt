package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.xenon

class MinecraftRenderAdapter : IGameRenderer {

    private val passes = ArrayList<RenderPass>()

    fun flush() {
        val type = currentType
        if (currentList.isNotEmpty() && type != null) {
            passes.add(RenderPass(type, currentList, currentHolograms))
            currentList = ArrayList()
        }
    }

    fun getRenderPasses(): List<RenderPass> {
        return passes
    }

    override fun isInCameraFrustum(box: Box): Boolean {
        return xenon.client.isInView(box)
    }

    private var currentList = ArrayList<IRenderPrimitive>()
    private var currentHolograms = ArrayList<Hologram>()
    private var currentType: RenderPipelineType? = null

    private fun addPrimitive(primitive: IRenderPrimitive, visibleThroughWalls: Boolean) {
        val type = primitive.getPipeline(visibleThroughWalls)
        if (currentType == null || currentType != type) {
            val currentType = currentType
            if (currentType != null && currentList.isNotEmpty()) passes.add(RenderPass(currentType, currentList, currentHolograms))
            currentHolograms = ArrayList()
            currentList = ArrayList()
            this.currentType = type
        }
        currentList.add(primitive)
    }

    override fun drawPrimitives(primitives: List<IRenderPrimitive>, visibleThroughWalls: Boolean) {
        for (primitive in primitives) {
            addPrimitive(primitive, visibleThroughWalls)
        }
    }

    fun drawHologram(hologram: Hologram) {
        currentHolograms.add(hologram)
    }

}