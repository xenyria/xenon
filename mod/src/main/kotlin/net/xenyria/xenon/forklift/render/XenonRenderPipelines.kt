package net.xenyria.xenon.forklift.render

import com.mojang.renderpearl.api.pipeline.*
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.oit.OitPipelineSet
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier
import net.xenyria.xenon.MOD_ID
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType

/**
 * Object that manages render pipelines for Xenon.
 * As of 26.3 there are two versions of every pipeline. (a classic one and one for OIT support)
 */
object XenonRenderPipelines {

    private data class PipelineEntry(val classic: RenderPipeline, val oit: OitPipelineSet)

    private val pipelines = HashMap<RenderPipelineType, PipelineEntry>()
    private val renderTypes = HashMap<RenderPipelineType, RenderType>()

    fun getRenderType(type: RenderPipelineType): RenderType {
        return renderTypes.getOrPut(type) {
            val entry = getEntry(type)
            val setup = RenderSetup.builder(entry.classic)
                .setOitPipelines(entry.oit)

            // Transparent quads need to be sorted (only relevant for classic transparency)
            if (entry.classic.primitiveTopology == PrimitiveTopology.QUADS)
                setup.sortOnUpload()

            // Create custom render type
            RenderType.create("${MOD_ID}_${type.name.lowercase()}", setup.createRenderSetup())
        }
    }

    private fun getEntry(type: RenderPipelineType): PipelineEntry {
        return requireNotNull(pipelines[type]) { "No render pipeline available for $type" }
    }

    private fun register(
        type: RenderPipelineType,
        baseSnippet: RenderPipeline.Snippet,
        oitSnippet: RenderPipeline.Snippet,
        depthConfig: DepthStencilState,
        isSeeThrough: Boolean
    ) {
        val name = type.name.lowercase()

        val classic = RenderPipeline.builder(baseSnippet)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/$name"))
            .withDepthStencilState(depthConfig)
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .build()

        // Required for rendering in 26.3 with "improved transparency" aka. OIT
        val oitBuilder = OitPipelineSet.builder(
            "${MOD_ID}_$name",
            RenderPipeline.builder(oitSnippet)
        )
        if (isSeeThrough) oitBuilder.withoutDepthTest()
        val oit = oitBuilder.build()

        RenderPipelines.register(classic)
        RenderPipelines.register(oit)
        pipelines[type] = PipelineEntry(classic, oit)
    }

    private fun initializeShapes() {
        register(
            RenderPipelineType.SHAPES_THROUGH_WALLS,
            RenderPipelines.DEBUG_FILLED_SNIPPET,
            RenderPipelines.OIT_DEBUG_FILLED_SNIPPET,
            DepthStencilState(
                CompareOp.ALWAYS_PASS, true,
                0F, 0F
            ),
            isSeeThrough = true
        )
        register(
            RenderPipelineType.SHAPES,
            RenderPipelines.DEBUG_FILLED_SNIPPET,
            RenderPipelines.OIT_DEBUG_FILLED_SNIPPET,
            DepthStencilState(
                CompareOp.GREATER_THAN_OR_EQUAL, true,
                0F, 0F
            ),
            isSeeThrough = false
        )
    }

    private fun initializeLines() {
        register(
            RenderPipelineType.LINES_THROUGH_WALLS,
            RenderPipelines.LINES_SNIPPET,
            RenderPipelines.OIT_LINES_SNIPPET,
            DepthStencilState(CompareOp.ALWAYS_PASS, true, 0F, 0F),
            isSeeThrough = true
        )
        register(
            RenderPipelineType.LINES,
            RenderPipelines.LINES_SNIPPET,
            RenderPipelines.OIT_LINES_SNIPPET,
            DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 0F, 0F),
            isSeeThrough = false
        )
    }

    fun initialize() {
        initializeShapes()
        initializeLines()
    }

}