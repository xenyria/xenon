package net.xenyria.xenon.message

import java.awt.Color

data class Message(val components: List<MessageComponent>) {

    constructor(vararg components: MessageComponent) : this(components.toList())

    companion object {
        val EMPTY: Message = Message(emptyList())
    }
}

data class MessageComponent(val text: String, val color: Color)
