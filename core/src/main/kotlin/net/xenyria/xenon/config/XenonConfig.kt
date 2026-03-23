package net.xenyria.xenon.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val LATEST_VERSION = 1

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

@Serializable
data class CameraSettings(
    @SerialName("mode") var mode: CameraMode
)

@Serializable
data class DeveloperSettings(
    @SerialName("enableGizmos") var enableGizmos: Boolean = true,
    @SerialName("enableShapes") var enableShapes: Boolean = true,
    @SerialName("enableOverlays") var enableOverlays: Boolean = true
)

@Serializable
data class MiscSettings(@SerialName("activityMode") var activityMode: RichPresenceMode = RichPresenceMode.TRUSTED_ONLY)

@Serializable
data class XenonConfig(
    @SerialName("version") val version: Int = LATEST_VERSION,
    @SerialName("camera") var camera: CameraSettings = CameraSettings(CameraMode.CHANGE_AND_LOCK),
    @SerialName("developer") var developer: DeveloperSettings = DeveloperSettings(),
    @SerialName("misc") var misc: MiscSettings = MiscSettings()
) {
    companion object {

        private val json = Json {
            ignoreUnknownKeys = true
        }

        fun decode(text: String): XenonConfig {
            return json.decodeFromString(text)
        }

        fun encode(config: XenonConfig): String {
            return json.encodeToString(config)
        }
    }
}
