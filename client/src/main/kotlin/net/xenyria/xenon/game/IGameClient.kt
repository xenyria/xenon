package net.xenyria.xenon.game

import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.config.XenonConfig
import net.xenyria.xenon.core.Box
import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.forklift.GameCamera
import net.xenyria.xenon.message.Message
import net.xenyria.xenon.protocol.IXenonPacket
import org.joml.Vector2d
import org.joml.Vector3dc
import java.util.*


/**
 * Abstract interface representing the game client.
 * Xenon requires access to the camera, mouse position, keyboard state, the ability to send and receive packets,
 * and the ability to render gizmos and shapes.
 */
interface IGameClient {

    /**
     * Returns a snapshot of the current camera state.
     */
    fun getCamera(): GameCamera

    /**
     * Returns the last known 2D mouse position in screen coordinates.
     */
    fun getMousePosition(): Vector2d

    /**
     * Sends a packet to the server. Xenon messages are expected to be sent as a plugin message.
     */
    fun sendPacket(packet: IXenonPacket)

    /**
     * Tests if a given box is within the camera's view frustum.
     */
    fun isInView(box: Box): Boolean

    /**
     * Projects a 3D world position to 2D screen coordinates.
     * The returned vector is expected to range between (-1, -1) and (1,1) as long as the point is visible on the
     * screen.
     */
    fun getScreenPosition(worldPosition: Vector3dc): Vector2d

    /**
     * Displays a message in the chat. (system message)
     */
    fun displayChatMessage(message: Message)

    /**
     * Various methods to check if modifier keys are currently pressed.
     */
    fun hasShiftDown(): Boolean
    fun hasControlDown(): Boolean
    fun hasAltDown(): Boolean

    /**
     * This updates the last X & Y values Minecraft has received from the mouse. We fake this to prevent the camera from
     * moving too much after the player has moved the gizmo.
     */
    fun updateInternalMousePosition(x: Double, y: Double)

    /**
     * Returns the UUID of the player. Used for checking if the player is allowed to edit a gizmo in edit mode.
     */
    fun getPlayerId(): UUID?

    /**
     * Returns the current version of the Xenon mod, used for compatibility checks when handshaking with the server.
     */
    fun getModVersion(): String

    /**
     * Updates the Discord activity data of the player.
     */
    fun updateActivity(activityData: ActivityData)

    /**
     * Update the Discord app ID used for the Activity API.
     */
    fun updateActivityAppId(appId: Long)

    /**
     * Updates the camera lock state and optionally switches to a new camera perspective.
     * If no new perspective is provided and the camera will be unlocked, the camera will switch to the last
     * perspective the player had before the camera was locked.
     */
    fun updateCameraLock(isLocked: Boolean, newMode: CameraPerspective?)

    /**
     * Requests a change in the camera perspective. The client may choose to ignore this request.
     */
    fun requestCameraPerspective(perspective: CameraPerspective)

    /**
     * Sets the camera perspective.
     */
    fun setCameraPerspective(perspective: CameraPerspective)

    /**
     * Returns the current camera perspective.
     */
    fun getCameraPerspective(): CameraPerspective

    /**
     * Starts a new session with the server. This is called after handshaking with the server has completed.
     * Depending on the server's response, the user may not be able to use all features of the mod.
     */
    fun startSession(canUseEditMode: Boolean)

    /**
     * Returns the configuration of the mod.
     */
    val xenonConfig: XenonConfig

}