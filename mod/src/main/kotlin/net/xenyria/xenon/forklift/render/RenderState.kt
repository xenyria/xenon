package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive

/**
 * Class that determines which shapes, gizmos and primitives should be rendered in the next frame.
 */
class RenderState {
    var shapes: List<RenderableShape> = ArrayList()
    var additionalPrimitives: List<IRenderPrimitive> = ArrayList()
    var gizmos: List<RenderableGizmo> = ArrayList()
}