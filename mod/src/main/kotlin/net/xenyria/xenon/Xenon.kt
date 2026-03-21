package net.xenyria.xenon

import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonInfo
import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.makeCenteredBox
import net.xenyria.xenon.forklift.Forklift
import net.xenyria.xenon.forklift.editor.EditorMode
import net.xenyria.xenon.forklift.editor.target.IEditorTarget
import net.xenyria.xenon.forklift.render.ForkliftRenderer
import net.xenyria.xenon.forklift.render.XenonRenderPipelines
import net.xenyria.xenon.forklift.render.primitive.BoxPrimitive
import net.xenyria.xenon.input.keyboard.KeyboardManager
import net.xenyria.xenon.input.mouse.fromLWJGL
import net.xenyria.xenon.mixin.KeyboardHandlerInvoker
import org.joml.Vector3d
import org.joml.Vector3dc
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.*

const val MOD_ID = "xenon"

class Xenon(val version: String) {

    val client = GameClient(this)
    val forklift = Forklift(client)
    val logger: Logger = LoggerFactory.getLogger("Xenon")

    private var _keyboard: KeyboardManager? = null
    val keyboard: KeyboardManager get() = requireNotNull(_keyboard) { "Keyboard functionality is not ready yet" }
    fun getKeyboardManagerOrNull(): KeyboardManager? = _keyboard

    init {
        logger.info("Starting up Xenon...")
    }

    fun initialize(game: Minecraft) {
        require(Minecraft.getInstance().window.handle() != 0L) { "Window is not initialized" }
        _keyboard = KeyboardManager(
            game.window.handle(),
            game.keyboardHandler as KeyboardHandlerInvoker
        )
        XenonRenderPipelines.initialize()
        ForkliftRenderer.initialize()

        var primRotation = Vector3d()
        val primitive = BoxPrimitive(
            makeCenteredBox(Vector3d(2.0, 4.0, 8.0), 1.0, 1.0),
            Color(Color.PINK.red, Color.PINK.green, Color.PINK.blue, 128),
            primRotation
        )

        val target = object : IEditorTarget {
            override val uuid: UUID = UUID.randomUUID()
            override var position: Vector3d
                get() = Vector3d(primitive.box.origin)
                set(value) {
                    primitive.box = primitive.box.moveTo(value.x, value.y, value.z)
                }
            override var scale: Vector3d
                get() = Vector3d(primitive.box.dimensions)
                set(value) {
                    primitive.box = primitive.box.resizeTo(
                        Vector3d(
                            maxOf(value.x, 0.0),
                            maxOf(value.y, 0.0),
                            maxOf(value.z, 0.0)
                        )
                    )
                }
            override var rotation: Vector3d
                get() = primRotation
                set(value) {
                    primRotation.set(value)
                }
            override val supportedModes: Set<EditorMode> get() = EditorMode.entries.toSet()
            override val supportedRotationAxes: Set<Axis> get() = Axis.entries.toSet()

            override fun synchronize(position: Vector3dc, rotation: Vector3dc, scale: Vector3dc) {
            }
        }

        forklift.editor.targetManager.updateTargets(listOf(target))
        ForkliftRenderer.updateAdditionalPrimitives(listOf(primitive))
        /*ForkliftRenderer.updateAdditionalPrimitives(
            ArrayList(
                listOf(
                    ConePrimitive(
                        Vector3d(0.0, 1.0, 0.0), Vector3d(0.0),
                        Color.GREEN, 0.5
                    ),
                    ConePrimitive(
                        Vector3d(0.0, -1.0, 0.0), Vector3d(0.0),
                        Color.GREEN.darker(), 0.5
                    ),
                    ConePrimitive(
                        Vector3d(1.0, 0.0, 0.0), Vector3d(0.0),
                        Color.RED, 0.5
                    ),
                    ConePrimitive(
                        Vector3d(-1.0, 0.0, 0.0), Vector3d(0.0),
                        Color.RED.darker().darker(), 0.5
                    ),
                    ConePrimitive(
                        Vector3d(0.0, 0.0, 1.0), Vector3d(0.0),
                        Color.CYAN, 0.5
                    ),
                    ConePrimitive(
                        Vector3d(0.0, 0.0, -1.0), Vector3d(0.0),
                        Color.CYAN.darker().darker(), 0.5
                    ),
                )
            )
        )*/
        logger.info("Xenon (v${version}) has been initialized.")
    }

    fun onTick() {
        forklift.onTick()
    }

    fun reset() {
        this.forklift.reset()
    }

    fun onMouseButton(mouseButtonInfo: MouseButtonInfo, action: Int): Boolean {
        if (game.screen != null) return false // 

        val event = fromLWJGL(mouseButtonInfo.button, action, mouseButtonInfo.modifiers)

        if (event.isRightMouseButton && event.isReleased) forklift.editor.leaveDragMode()

        if (forklift.editor.onMouseButton(event) && forklift.editor.isActive) {
            keyboard.releaseAllKeys()
            return true
        }
        return false
    }

    companion object {
        private var _xenon: Xenon? = null
        val instance: Xenon get() = requireNotNull(_xenon) { "Xenon is not initialized" }

        fun getOrNull(): Xenon? {
            return _xenon
        }

        fun create(version: String) {
            _xenon = Xenon(version)
        }
    }
}

val game: Minecraft get() = Minecraft.getInstance()
val xenon: Xenon get() = Xenon.instance
val forklift: Forklift get() = xenon.forklift