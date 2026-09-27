package net.xenyria.xenon.forklift.render

import com.mojang.renderpearl.api.pipeline.*
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.xenyria.xenon.MOD_ID
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType

/**
 * Object that manages render pipelines for Xenon.
 */
object XenonRenderPipelines {

    private val pipelines = HashMap<RenderPipelineType, RenderPipeline>()

    fun getPipeline(type: RenderPipelineType): RenderPipeline {
        return requireNotNull(pipelines[type]) { "No render pipeline available for $type" }
    }

    private fun initializeShapes() {
        pipelines[RenderPipelineType.SHAPES_THROUGH_WALLS] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/shapes_through_walls"))
                .withDepthStencilState(DepthStencilState(CompareOp.ALWAYS_PASS, false, 0F, 0F))
                .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
                .build()
        )
        pipelines[RenderPipelineType.SHAPES] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/shapes_regular"))
                .withDepthStencilState(DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0F, 0F))
                .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
                .build()
        )
    }

    private fun initializeLines() {
        pipelines[RenderPipelineType.LINES_THROUGH_WALLS] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/lines_through_walls"))
                .withDepthStencilState(DepthStencilState(CompareOp.ALWAYS_PASS, false, 0F, 0F))
                .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
                .build()
        )
        pipelines[RenderPipelineType.LINES] = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/lines_regular"))
                .withDepthStencilState(DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0F, 0F))
                .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
                .build()
        )
    }

    fun initialize() {
        initializeShapes()
        initializeLines()
    }

}