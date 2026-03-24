@file:Suppress("UNCHECKED_CAST")

package net.xenyria.xenon.forklift.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.network.chat.Component
import net.xenyria.xenon.config.XenonConfig
import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.render.pipeline.RenderPipelineType
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.forklift.render.shape.ShapeRenderers
import net.xenyria.xenon.shape.IEditorShape
import net.xenyria.xenon.util.parseComponentFromJSON
import net.xenyria.xenon.xenon
import org.joml.Vector3dc

/**
 * Represents a renderable debug shape. Shapes can also have text rendered above them
 */
data class RenderableShape(val shape: IEditorShape<*>, val lines: List<Component>) {
    constructor(shape: IEditorShape<*>) : this(shape, shape.textLines.map { parseComponentFromJSON(it) })
}

/**
 * Represents a debug hologram (text) rendered at a given position
 */
data class Hologram(val position: Vector3dc, val lines: List<Component>)

/**
 * Represents a render pass (which pipeline to use, what primitives to draw, ...)
 */
data class RenderPass(
    val pipelineType: RenderPipelineType, // Which pipeline to use for rendering the provided primitives
    val primitives: List<IRenderPrimitive>,
    val holograms: List<Hologram>
) {
    fun getPipeline(): RenderPipeline {
        return XenonRenderPipelines.getPipeline(pipelineType)
    }
}

object ForkliftShapeExtractor {

    fun extract(
        config: XenonConfig,
        primitives: List<IRenderPrimitive>,
        shapes: List<RenderableShape>,
        gizmos: List<RenderableGizmo>
    ): List<RenderPass> {
        val renderAdapter = MinecraftRenderAdapter()
        renderAdapter.drawPrimitives(primitives, false)

        val forklift = xenon.getForkliftOrNull()
        if (forklift != null && forklift.editor.isActive && config.developer.enableGizmos)
            for (gizmo in gizmos) {
                if (gizmo.error != null || !xenon.client.isInView(gizmo.cullingBox)) continue
                gizmo.target.render(renderAdapter, gizmo.isSelected, gizmo.isTransparent)
            }

        if (config.developer.enableShapes) {
            for (renderShape in shapes) {
                val shape = renderShape.shape
                val renderer = ShapeRenderers.getRenderer(shape.type) as IShapeRenderer<IEditorShape<*>>
                if (!renderer.drawShape(renderAdapter, shape)) continue
                if (renderShape.lines.isNotEmpty()) renderAdapter.drawHologram(
                    Hologram(
                        shape.textDisplayOrigin,
                        renderShape.lines
                    )
                )
            }
        }
        renderAdapter.flush()
        return renderAdapter.getRenderPasses()
    }


}