package net.xenyria.xenon.message

import java.awt.Color

/**
 * Represents a text message that can be sent to a player, consisting of an array of message components.
 */
data class Message(val components: List<MessageComponent>) {
    constructor(vararg components: MessageComponent) : this(components.toList())
}

/**
 * A single component in a message.
 * If [isTranslated] is set to false, [text] will be treated as a translation placeholder.
 */
data class MessageComponent(val text: String, val color: Color, val isTranslated: Boolean = false)
