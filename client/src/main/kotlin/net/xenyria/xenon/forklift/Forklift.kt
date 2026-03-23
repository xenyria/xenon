package net.xenyria.xenon.forklift

import net.xenyria.xenon.forklift.editor.Editor
import net.xenyria.xenon.forklift.editor.IGameClient

/**
 * Main class for the map editor features provided by Xenon (formerly known as Forklift)
 */
class Forklift(val client: IGameClient) {

    val editor = Editor(client)

    fun onTick() {
        editor.onTick()
    }

    fun reset() {
        editor.reset()
    }

}