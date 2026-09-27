@file:Suppress("UNCHECKED_CAST")

package net.xenyria.xenon.forklift.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.StagedVertexBuffer
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
import java.awt.Color
import java.util.*

val COLOR_MODULATOR: Vector4f = Vector4f(1f, 1f, 1f, 1f)
val MODEL_OFFSET: Vector3f = Vector3f()
val TEXTURE_MATRIX: Matrix4f = Matrix4f()

/**
 * Class that provides an interface for rendering shapes, gizmos and other primitives.
 *
 * Huge parts of this class are based around rendering examples from Fabric:
 * https://github.com/FabricMC/fabric-docs/blob/main/reference/latest/src/client/java/com/example/docs/rendering/CustomRenderPipeline.java
 */
object ForkliftRenderer {

    private val renderState = RenderState()
    private var extractedPasses: List<RenderPass> = emptyList()
    private val stagedVertexBuffer = StagedVertexBuffer(
        { "Xenon Forklift Debug Render Buffer" },
        RenderType.BIG_BUFFER_SIZE // Appears to be 4 MiB
    )

    fun updateGizmos(gizmos: List<RenderableGizmo>) {
        renderState.gizmos = gizmos.toList()
    }

    fun updateShapes(shapes: List<RenderableShape>) {
        renderState.shapes = shapes.toList()
    }

    fun initialize() {
        LevelExtractionEvents.END_EXTRACTION.register { _ ->
            extractedPasses = ForkliftShapeExtractor.extract(
                XenonClientConfig.config,
                renderState.additionalPrimitives,
                renderState.shapes,
                renderState.gizmos
            )
        }
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register { context ->
            for ((index, pass) in extractedPasses.withIndex()) {
                drawPrimitives(pass, context, index)
            }
        }
        LevelRenderEvents.END_MAIN.register { context ->
            // Text is rendered last.
            // This ensures that text is always rendered on top of primitives
            drawGizmoErrors(context)
            for (pass in extractedPasses) drawShapeText(pass, context)
        }
    }

    private fun drawShapeText(pass: RenderPass, context: LevelRenderContext) {
        if (!xenon.config.developer.enableShapes) return
        val renderer = WorldTextRenderer(context)
        for ((position, lines) in pass.holograms) {
            val scale = 1.0F / 48.0F // Scaled in a way to make one text pixel as large as 1/48 of a block
            renderer.renderCentered(position, lines, true, scale)
        }
    }

    private fun drawGizmoErrors(context: LevelRenderContext) {
        val forklift = xenon.getForkliftOrNull() ?: return
        val config = xenon.config
        if (!forklift.editorClient.isActive || !config.developer.enableGizmos) return
        val renderer = WorldTextRenderer(context)

        for ((target, _, _, errorMessage) in renderState.gizmos) {
            val error = errorMessage ?: continue
            renderer.renderCentered(
                target.target.position,
                listOf(
                    Message(MessageComponent(error, Color.RED, true))
                        .toComponent()
                ),
                seeThrough = true,
                scale = 1.0F / 64.0F,
            )
        }
    }

    private fun drawPrimitives(
        renderPass: RenderPass,
        context: LevelRenderContext,
        iteration: Int
    ) {
        if (renderPass.primitives.isEmpty()) return

        val pipeline = renderPass.getPipeline()
        val binding = requireNotNull(pipeline.getVertexFormatBinding(0))

        val matrices: PoseStack = context.poseStack()
        val camera: Vec3 = context.levelState().cameraRenderState.pos

        matrices.pushPose()
        matrices.translate(-camera.x, -camera.y, -camera.z)

        val primitive = pipeline.primitiveTopology
        val draw = stagedVertexBuffer.appendDraw(
            binding,
            primitive,
            if (primitive == PrimitiveTopology.QUADS)
                RenderSystem.getProjectionType().vertexSorting()
            else
                null
        )
        val positionMatrix = requireNotNull(context.poseStack().last()) { "Position matrix is not available" }

        val builder = stagedVertexBuffer.getVertexBuilder(draw)
        for (primitive in renderPass.primitives) {
            renderPrimitiveToBuffer(positionMatrix.pose(), primitive, builder)
        }

        matrices.popPose()
        stagedVertexBuffer.upload()

        val info = stagedVertexBuffer.getExecuteInfo(draw)
        if (info != null) draw(
            Minecraft.getInstance(), info, pipeline,
            renderPass.pipelineType.toString().lowercase(), iteration
        )

        stagedVertexBuffer.endFrame()
    }

    private fun renderPrimitiveToBuffer(
        positionMatrix: Matrix4fc,
        primitive: IRenderPrimitive,
        builder: VertexConsumer
    ) {
        for (vertex in primitive.extractVertices()) {
            val bufferVertex = builder.addVertex(
                positionMatrix, vertex.x.toFloat(), vertex.y.toFloat(), vertex.z.toFloat()
            )
            bufferVertex.setColor(vertex.red, vertex.green, vertex.blue, vertex.alpha)

            val lineWidth = vertex.lineWidth
            if (lineWidth != null) {
                bufferVertex.setLineWidth(lineWidth)
            }

            val normal = vertex.normal
            if (normal != null) bufferVertex.setNormal(
                normal.x().toFloat(),
                normal.y().toFloat(),
                normal.z().toFloat()
            )
        }
    }

    fun destroy() {
        stagedVertexBuffer.close()
    }

    private fun draw(
        client: Minecraft,
        info: StagedVertexBuffer.ExecuteInfo,
        pipeline: RenderPipeline,
        id: String,
        iteration: Int
    ) {
        val dynamicTransforms = RenderSystem.getDynamicUniforms()
            .writeTransform(
                RenderSystem.getModelViewMatrixCopy(),
                COLOR_MODULATOR,
                MODEL_OFFSET,
                TEXTURE_MATRIX
            )

        val mainTarget = client.gameRenderer.mainRenderTarget()
        val colorTexture = mainTarget.getColorTextureView()

        checkNotNull(colorTexture)

        RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
                { "$MOD_ID/$id/$iteration" },
                colorTexture,
                Optional.empty(),
                mainTarget.getDepthTextureView(),
                OptionalDouble.empty()
            ).use { renderPass ->
                val pipeline = RenderSystem.getCompiledPipeline(pipeline)
                renderPass.setPipeline(pipeline)
                RenderSystem.bindDefaultUniforms(renderPass)
                renderPass.setUniform("DynamicTransforms", dynamicTransforms)

                // Bind texture if applicable:
                // Sampler0 is used for texture inputs in vertices
                renderPass.setVertexBuffer(0, info.vertexBuffer().slice())
                renderPass.setIndexBuffer(info.indexBuffer(), info.indexType())

                // The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex size
                renderPass.drawIndexed(
                    info.indexCount(),
                    1, info.firstIndex(), info.baseVertex(),
                    0
                )
            }
    }


}