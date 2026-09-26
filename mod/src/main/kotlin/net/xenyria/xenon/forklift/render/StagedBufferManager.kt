package net.xenyria.xenon.forklift.render

import net.minecraft.client.renderer.StagedVertexBuffer
import net.minecraft.client.renderer.rendertype.RenderType
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType

class StagedBufferManager {

    data class Buffer(val pipelineType: RenderPipelineType, val stagedBuffer: StagedVertexBuffer)

    private val buffers = HashMap<RenderPipelineType, Buffer>()

    fun getBuffer(pipelineType: RenderPipelineType): StagedVertexBuffer {
        return requireNotNull(buffers[pipelineType]).stagedBuffer
    }

    init {
        for (pipeline in RenderPipelineType.entries) {
            buffers[pipeline] = Buffer(
                pipeline,
                StagedVertexBuffer(
                    { "forklift/$pipeline" },
                    RenderType.BIG_BUFFER_SIZE / 2
                )
            )
        }
    }

    fun destroy() {
        buffers.values.forEach { it.stagedBuffer.close() }
    }

}