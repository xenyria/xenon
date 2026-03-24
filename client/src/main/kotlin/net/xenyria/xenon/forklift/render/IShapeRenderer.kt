package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.shape.IEditorShape

interface IShapeRenderer<Shape : IEditorShape<*>> {

    /**
     * Attempts to extract vertices from the provided shape.
     * @return True, if the shape was rendered. False otherwise (out of camera range, not within camera frustum, ...)
     */
    fun drawShape(renderer: IGameRenderer, shape: Shape): Boolean
}