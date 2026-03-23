package net.xenyria.xenon.message

import java.awt.Color

val FORKLIFT_COLOR = Color(227, 103, 36)
val TEXT_COLOR = Color(219, 182, 162)

object MessageFormatter {

    fun formatForkliftMessage(text: String): Message {
        return Message(
            MessageComponent("[Forklift] ", FORKLIFT_COLOR),
            MessageComponent(text, TEXT_COLOR, true)
        )
    }

}