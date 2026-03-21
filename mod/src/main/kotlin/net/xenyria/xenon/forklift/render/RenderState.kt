package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.forklift.editor.RenderableGizmo
import net.xenyria.xenon.forklift.render.primitive.IRenderPrimitive
import net.xenyria.xenon.shape.IEditorShape

class RenderState {
    var shapes: List<IEditorShape<*>> = ArrayList()
    var additionalPrimitives: List<IRenderPrimitive> = ArrayList()
    var gizmos: List<RenderableGizmo> = ArrayList()
}