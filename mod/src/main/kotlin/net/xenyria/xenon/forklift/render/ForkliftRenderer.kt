@file:Suppress("UNCHECKED_CAST")

package net.xenyria.xenon.forklift.render

import com.mojang.blaze3d.buffers.GpuBuffer
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.blaze3d.vertex.MeshData.DrawState
import com.mojang.blaze3d.vertex.VertexFormat.IndexType
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MappableRingBuffer
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.world.phys.Vec3
import net.xenyria.xenon.MOD_ID
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import net.xenyria.xenon.util.toComponent
import net.xenyria.xenon.xenon
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.MemoryUtil
import java.awt.Color
import java.util.*


val COLOR_MODULATOR: Vector4f = Vector4f(1f, 1f, 1f, 1f)
val MODEL_OFFSET: Vector3f = Vector3f()
val TEXTURE_MATRIX: Matrix4f = Matrix4f()

/**
 * Huge parts of this class are based around rendering examples from Fabric's mod docs:
 * https://docs.fabricmc.net/develop/rendering/world (let's just hope this class will survive the vulkan rewrite)
 */
object ForkliftRenderer {

    private val _allocator = ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE)
    private val _renderState = RenderState()

    fun updateGizmos(gizmos: List<RenderableGizmo>) {
        _renderState.gizmos = gizmos.toList()
    }

    fun updateShapes(shapes: List<RenderableShape>) {
        _renderState.shapes = shapes.toList()
    }

    fun updateAdditionalPrimitives(primitives: List<IRenderPrimitive>) {
        _renderState.additionalPrimitives = primitives.toList()
    }

    private var _vertexBuffer: MappableRingBuffer? = null
    val vertexBuffer: MappableRingBuffer
        get() = requireNotNull(_vertexBuffer) { "Vertex buffer is not available" }

    fun initialize() {
        WorldRenderEvents.END_MAIN.register { context -> render(context) }
    }

    fun render(context: WorldRenderContext) {
        val passes = ForkliftShapeExtractor.extract(
            XenonClientConfig.config,
            _renderState.additionalPrimitives,
            _renderState.shapes,
            _renderState.gizmos
        )
        // First step: Draw all primitives
        for (pass in passes) drawPrimitives(pass, context)

        // Lastly, also draw all shape holograms/in-world debug texts
        drawGizmoErrors(context)
        for (pass in passes) drawShapeText(pass, context)
    }

    private fun drawShapeText(pass: RenderPass, context: WorldRenderContext) {
        if (!xenon.config.developer.enableShapes) return
        val renderer = WorldTextRenderer(context)
        for (hologram in pass.holograms) {
            val scale = 1.0F / 48.0F // Scaled in a way to make one text pixel as large as 1/48 of a block
            renderer.renderCentered(hologram.position, hologram.lines, true, scale)
        }
    }

    private fun drawGizmoErrors(context: WorldRenderContext) {
        val forklift = xenon.getForkliftOrNull() ?: return
        val config = xenon.config
        if (!forklift.editor.isActive || !config.developer.enableGizmos) return
        val renderer = WorldTextRenderer(context)

        for (gizmo in _renderState.gizmos) {
            val error = gizmo.error ?: continue
            renderer.renderCentered(
                gizmo.target.target.position,
                listOf(
                    Message(MessageComponent(error, Color.RED, true)).toComponent()
                ),
                true
            )
        }
    }

    private fun drawPrimitives(renderPass: RenderPass, context: WorldRenderContext) {
        val pipeline = renderPass.getPipeline()
        val builder = BufferBuilder(_allocator, pipeline.vertexFormatMode, pipeline.vertexFormat)

        val matrices: PoseStack = context.matrices()
        val camera: Vec3 = context.worldState().cameraRenderState.pos

        matrices.pushPose()
        matrices.translate(-camera.x, -camera.y, -camera.z)

        val positionMatrix = requireNotNull(context.matrices().last()) { "Position matrix is not available" }
        for (primitive in renderPass.primitives) {
            renderPrimitiveToBuffer(positionMatrix.pose(), primitive, builder)
        }

        matrices.popPose()

        // Build the buffer
        val builtBuffer: MeshData = builder.build() ?: return
        val drawParameters = builtBuffer.drawState()
        val format = drawParameters.format()

        val vertices = upload(drawParameters, format, builtBuffer)

        draw(Minecraft.getInstance(), pipeline, builtBuffer, drawParameters, vertices, format)
        vertexBuffer.rotate()
    }

    private fun renderPrimitiveToBuffer(positionMatrix: Matrix4fc, primitive: IRenderPrimitive, builder: BufferBuilder) {
        for (vertex in primitive.getVertices()) {
            val bufferVertex = builder.addVertex(
                positionMatrix, vertex.x.toFloat(), vertex.y.toFloat(), vertex.z.toFloat()
            )
            bufferVertex.setColor(vertex.red, vertex.green, vertex.blue, vertex.alpha)

            val lineWidth = vertex.lineWidth
            if (lineWidth != null) bufferVertex.setLineWidth(lineWidth)

            val normal = vertex.normal
            if (normal != null) bufferVertex.setNormal(
                normal.x().toFloat(),
                normal.y().toFloat(),
                normal.z().toFloat()
            )
        }
    }

    fun destroy() {
        _vertexBuffer?.close()
        _vertexBuffer = null
        _allocator.close()
    }

    private fun createVertexBuffer(size: Int) {
        if (_vertexBuffer == null || vertexBuffer.size() < size) {
            if (_vertexBuffer != null) {
                vertexBuffer.close()
            }
            _vertexBuffer = MappableRingBuffer(
                { "$MOD_ID example render pipeline" }, GpuBuffer.USAGE_VERTEX or GpuBuffer.USAGE_MAP_WRITE,
                size
            )
        }
    }

    private fun upload(drawParameters: DrawState, format: VertexFormat, builtBuffer: MeshData): GpuBuffer {
        // Calculate the size needed for the vertex buffer
        val vertexBufferSize = drawParameters.vertexCount() * format.vertexSize

        // Initialize or resize the vertex buffer as needed
        createVertexBuffer(vertexBufferSize)

        // Copy vertex data into the vertex buffer
        val commandEncoder = RenderSystem.getDevice().createCommandEncoder()

        commandEncoder.mapBuffer(
            vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining().toLong()),
            false, true
        ).use { mappedView ->
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), mappedView.data())
        }
        return vertexBuffer.currentBuffer()
    }

    private fun draw(client: Minecraft, pipeline: RenderPipeline, builtBuffer: MeshData, drawParameters: DrawState, vertices: GpuBuffer, format: VertexFormat) {
        val indices: GpuBuffer?
        val indexType: IndexType?

        if (pipeline.vertexFormatMode == VertexFormat.Mode.QUADS) {
            // Sort the quads if there is translucency
            builtBuffer.sortQuads(_allocator, RenderSystem.getProjectionType().vertexSorting())
            // Upload the index buffer
            indices = pipeline.vertexFormat.uploadImmediateIndexBuffer(builtBuffer.indexBuffer()!!)
            indexType = builtBuffer.drawState().indexType()
        } else {
            // Use the general shape index buffer for non-quad draw modes
            val shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.vertexFormatMode)
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount())
            indexType = shapeIndexBuffer.type()
        }

        // Actually execute the draw
        val dynamicTransforms = RenderSystem.getDynamicUniforms()
            .writeTransform(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX)
        RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
                { "$MOD_ID example render pipeline rendering" },
                client.mainRenderTarget.getColorTextureView()!!,
                OptionalInt.empty(),
                client.mainRenderTarget.getDepthTextureView(),
                OptionalDouble.empty()
            ).use { renderPass ->
                renderPass.setPipeline(pipeline)
                RenderSystem.bindDefaultUniforms(renderPass)
                renderPass.setUniform("DynamicTransforms", dynamicTransforms)

                // Bind texture if applicable:
                // Sampler0 is used for texture inputs in vertices
                // renderPass.bindTexture("Sampler0", textureSetup.texure0(), textureSetup.sampler0());
                renderPass.setVertexBuffer(0, vertices)
                renderPass.setIndexBuffer(indices, indexType)

                // The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex size
                renderPass.drawIndexed(0 / format.getVertexSize(), 0, drawParameters.indexCount(), 1)
            }
        builtBuffer.close()
    }

}