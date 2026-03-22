package net.xenyria.xenon

import net.xenyria.xenon.forklift.Forklift
import net.xenyria.xenon.forklift.editor.IGameClient

class Session(client: IGameClient) {

    val forklift = Forklift(client)

}