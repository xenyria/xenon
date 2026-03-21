package net.xenyria.xenon.message

import java.awt.Color

data class Message(val components: List<MessageComponent>) {
    companion object {
        val EMPTY: Message = Message(emptyList())
    }
}

data class MessageComponent(val text: String, val color: Color)
