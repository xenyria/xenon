package net.xenyria.xenon.forklift.editor.input


/**
 * Represents a mouse button event.
 */
class MouseButtonEvent(val button: MouseButton?, val action: MouseButtonAction?) {
    val isRightMouseButton: Boolean get() = button == MouseButton.RIGHT
    val isReleased: Boolean get() = action == MouseButtonAction.RELEASE
    val isPressed: Boolean get() = action == MouseButtonAction.PRESS
    val isLeftMouseButton: Boolean get() = button == MouseButton.LEFT

    enum class MouseButton {
        LEFT,
        RIGHT,
        MIDDLE
    }

    enum class MouseButtonAction {
        PRESS,
        RELEASE
    }

    override fun toString(): String {
        return "MouseButtonEvent{" +
                "button=" + button +
                ", action=" + action +
                '}'
    }
}