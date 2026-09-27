package net.xenyria.xenon.forklift

import net.xenyria.xenon.forklift.editor.EditorClientState
import net.xenyria.xenon.forklift.editor.IEditorGameClient

/**
 * Main class for the level editor features provided by Xenon, more commonly referred to as "Forklift".
 */
class Forklift(client: IEditorGameClient) {

    val editorClient = EditorClientState(client)

    fun onTick() {
        editorClient.onTick()
    }

    fun reset() {
        editorClient.reset()
    }

}