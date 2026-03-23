package net.xenyria.xenon.config

enum class CameraMode(val key: String) {
    DISABLED("xenon_camera_disabled"),
    CHANGE("xenon_camera_change"),
    CHANGE_AND_LOCK("xenon_camera_change_and_lock")
}

enum class RichPresenceMode(val key: String) {
    NONE("xenon_drpc_none"),
    TRUSTED_ONLY("xenon_drpc_trusted_only"),
    ALL("xenon_drpc_all_servers")
}

data class CameraSettings(
    var mode: CameraMode
)

data class DeveloperSettings(
    var enableForklift: Boolean = true,
    var enableShapes: Boolean = true,
    var enableGizmos: Boolean = true,
    var enableOverlays: Boolean = true
) {
}

data class MiscSettings(var discordActivityMode: RichPresenceMode = RichPresenceMode.TRUSTED_ONLY)

data class XenonConfig(
    var camera: CameraSettings = CameraSettings(CameraMode.CHANGE_AND_LOCK),
    var developer: DeveloperSettings = DeveloperSettings(),
    var misc: MiscSettings = MiscSettings()
)
