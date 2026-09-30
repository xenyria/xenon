@file:Suppress("UNCHECKED_CAST")

package net.xenyria.xenon.forklift.render

import com.mojang.blaze3d.vertex.VertexConsumer
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.message.MessageComponent
import net.xenyria.xenon.util.toComponent
import net.xenyria.xenon.xenon
import org.joml.Matrix4fc
import java.awt.Color


// For context: The default Minecraft font is 8x8 pixels.

// Scales text in a way to make one text pixel as large as 1/48 of a block.
private const val SHAPE_TEXT_SCALE = 1.0F / 48.0F

// Scales text in a way to make one text pixel as large as 1/64 of a block.
private const val GIZMO_ERROR_TEXT_SCALE = 1.0F / 64.0F

/**
 * Class that provides an interface for rendering shapes, gizmos and other primitives.
 * Primitive & text draw commands are extracted in the END_EXTRACTION event and later dispatched during the
 * COLLECT_SUBMITS render event.
 */
object ForkliftRenderer {

    private val renderState = RenderState()
    private var extractedPasses: List<RenderPass> = emptyList()

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
        LevelRenderEvents.COLLECT_SUBMITS.register { context ->
            for (pass in extractedPasses) submitPrimitives(pass, context)
            submitGizmoErrors(context)
            for (pass in extractedPasses) submitShapeText(pass, context)
        }
    }

    private fun submitShapeText(pass: RenderPass, context: LevelRenderContext) {
        if (!xenon.config.developer.enableShapes) return
        val renderer = WorldTextRenderer(context)
        for ((position, lines) in pass.holograms) {
            renderer.renderCentered(position, lines, true, SHAPE_TEXT_SCALE)
        }
    }

    private fun submitGizmoErrors(context: LevelRenderContext) {
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
                scale = GIZMO_ERROR_TEXT_SCALE,
            )
        }
    }

    private fun submitPrimitives(renderPass: RenderPass, context: LevelRenderContext) {
        if (renderPass.primitives.isEmpty()) return

        val renderType = XenonRenderPipelines.getRenderType(renderPass.pipelineType)
        val primitives = renderPass.primitives
        val poseStack = context.poseStack()
        val camera = context.levelState().cameraRenderState.pos

        poseStack.pushPose()
        poseStack.translate(-camera.x, -camera.y, -camera.z)
        context.submitNodeCollector().submitCustomGeometry(poseStack, renderType) { pose, builder ->
            for (primitive in primitives) {
                renderPrimitiveToBuffer(pose.pose(), primitive, builder)
            }
        }
        poseStack.popPose()
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
        extractedPasses = emptyList()
    }

}