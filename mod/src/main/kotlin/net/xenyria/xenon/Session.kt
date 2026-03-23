package net.xenyria.xenon

import net.xenyria.xenon.camera.CameraPerspective
import net.xenyria.xenon.config.CameraMode
import net.xenyria.xenon.config.RichPresenceMode
import net.xenyria.xenon.discord.ActivityData
import net.xenyria.xenon.discord.DiscordActivityManager
import net.xenyria.xenon.forklift.Forklift
import net.xenyria.xenon.forklift.editor.IGameClient
import net.xenyria.xenon.util.getCurrentServer

class Session(private val client: IGameClient, editModeAvailable: Boolean) {

    val isTrusted: Boolean = TrustedServers.isTrusted(getCurrentServer())
    val forklift: Forklift? = if (editModeAvailable) Forklift(client) else null
    val activityManager = DiscordActivityManager()

    private var _isCameraModeLocked = false
    private var _previousCameraMode: CameraPerspective? = null

    fun isCameraModeLocked(): Boolean {
        return _isCameraModeLocked
    }

    @Synchronized
    fun canApplyActivity(): Boolean {
        val mode = client.xenonConfig.misc.activityMode
        if (mode == RichPresenceMode.NONE) return false
        if (mode == RichPresenceMode.TRUSTED_ONLY) return isTrusted
        return true
    }

    @Synchronized
    fun updateActivityAppId(appId: Long) {
        activityManager.updateAppId(appId)
    }

    @Synchronized
    fun updateActivity(activityData: ActivityData) {
        if (canApplyActivity()) activityManager.update(activityData)
    }

    @Synchronized
    fun destroy() {
        activityManager.stop()
    }

    @Synchronized
    fun stopActivity() {
        activityManager.stop()
    }

    @Synchronized
    fun requestCameraPerspective(perspective: CameraPerspective) {
        if (client.xenonConfig.camera.mode == CameraMode.DISABLED) return
        client.setCameraPerspective(perspective)
    }

    @Synchronized
    fun updateCameraLock(locked: Boolean, mode: CameraPerspective?) {
        if (client.xenonConfig.camera.mode != CameraMode.CHANGE_AND_LOCK) return
        if (!_isCameraModeLocked && locked) {
            _isCameraModeLocked = true
            _previousCameraMode = client.getCameraPerspective()
        } else if (!locked && _isCameraModeLocked) {
            _isCameraModeLocked = false
            val prevMode = _previousCameraMode
            if (mode == null && prevMode != null) {
                client.setCameraPerspective(prevMode)
            }
        }
        if (mode != null) client.setCameraPerspective(mode)
    }


}