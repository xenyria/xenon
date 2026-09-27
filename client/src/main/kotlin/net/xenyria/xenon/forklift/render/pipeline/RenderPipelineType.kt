package net.xenyria.xenon.forklift.render.pipeline

/**
 * Enum representing different render pipelines for rendering shapes.
 * A mod implementation must support all of these pipelines.
 */
enum class RenderPipelineType {
    SHAPES_THROUGH_WALLS,
    SHAPES,
    LINES_THROUGH_WALLS,
    LINES
}