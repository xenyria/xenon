package net.xenyria.xenon.forklift.render

import net.xenyria.xenon.shape.IEditorShape

interface IShapeRenderer<Shape : IEditorShape<*>> {
    fun drawShape(renderer: IGameRenderer, shape: Shape)
}