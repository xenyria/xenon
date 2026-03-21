package net.xenyria.xenon.forklift.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.xenyria.xenon.MOD_ID
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType

object XenonRenderPipelines {

    private val _pipelines = HashMap<RenderPipelineType, RenderPipeline>()

    fun getPipeline(type: RenderPipelineType): RenderPipeline {
        return requireNotNull(_pipelines[type]) { "No render pipeline available for $type" }
    }

    private fun initializeShapes() {
        _pipelines[RenderPipelineType.SHAPES_THROUGH_WALLS] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/shapes_through_walls"))
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build()
        )
        _pipelines[RenderPipelineType.SHAPES] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/shapes_regular"))
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withDepthWrite(true)
                .build()
        )
    }

    private fun initializeLines() {
        _pipelines[RenderPipelineType.LINES_THROUGH_WALLS] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/lines_through_walls"))
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build()
        )
        _pipelines[RenderPipelineType.LINES] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/lines_regular"))
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .build()
        )
    }

    fun initialize() {
        initializeShapes()
        initializeLines()
    }

}