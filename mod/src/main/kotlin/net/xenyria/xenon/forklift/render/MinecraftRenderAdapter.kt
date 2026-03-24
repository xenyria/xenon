package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.core.Box
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.xenon

class MinecraftRenderAdapter : IGameRenderer {

    private val _passes = ArrayList<RenderPass>()

    fun flush() {
        val type = _currentType
        if (_currentList.isNotEmpty() && type != null) {
            _passes.add(RenderPass(type, _currentList, _currentHolograms))
            _currentList = ArrayList()
        }
    }

    fun getRenderPasses(): List<RenderPass> {
        return _passes
    }

    override fun isInCameraFrustum(box: Box): Boolean {
        return xenon.client.isInView(box)
    }

    private var _currentList = ArrayList<IRenderPrimitive>()
    private var _currentHolograms = ArrayList<Hologram>()
    private var _currentType: RenderPipelineType? = null

    private fun addPrimitive(primitive: IRenderPrimitive, visibleThroughWalls: Boolean) {
        val type = primitive.getPipeline(visibleThroughWalls)
        if (_currentType == null || _currentType != type) {
            val currentType = _currentType
            if (currentType != null && _currentList.isNotEmpty()) _passes.add(RenderPass(currentType, _currentList, _currentHolograms))
            _currentHolograms = ArrayList()
            _currentList = ArrayList()
            _currentType = type
        }
        _currentList.add(primitive)
    }

    override fun drawPrimitives(primitives: List<IRenderPrimitive>, visibleThroughWalls: Boolean) {
        for (primitive in primitives) {
            addPrimitive(primitive, visibleThroughWalls)
        }
    }

    fun drawHologram(hologram: Hologram) {
        _currentHolograms.add(hologram)
    }

}