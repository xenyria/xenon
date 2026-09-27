package net.xenyria.xenon.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val LATEST_VERSION = 1

/**
 * Camera mode setting options.
 */
enum class CameraMode(val key: String) {
    /**
     * Do not allow the server to change the camera perspective.
     */
    DISABLED("xenon_camera_disabled"),

    /**
     * Allows the server to change the camera perspective, but does not lock it.
     */
    CHANGE("xenon_camera_change"),

    /**
     * Allows the server to change the camera perspective and locks it.
     * (Inputs made by the player are discarded until the server unlocks the camera perspective)
     */
    CHANGE_AND_LOCK("xenon_camera_change_and_lock")
}

/**
 * Discord activity setting options.
 */
enum class DiscordActivityMode(val key: String) {
    /**
     * Do not allow any Discord activity to be sent to the server.
     */
    NONE("xenon_drpc_none"),

    /**
     * Only accept Discord activity from trusted servers. (See [net.xenyria.xenon.TrustedServers])
     */
    TRUSTED_ONLY("xenon_drpc_trusted_only"),

    /**
     * Allows all servers to send Discord activity to the client.
     */
    ALL("xenon_drpc_all_servers")
}

@Serializable
data class CameraSettings(
    @SerialName("mode") var mode: CameraMode
)

/**
 * Additional developer settings that can be enabled or disabled.
 */
@Serializable
data class DeveloperSettings(
    @SerialName("enableGizmos") var enableGizmos: Boolean = true,
    @SerialName("enableShapes") var enableShapes: Boolean = true,
    @SerialName("enableOverlays") var enableOverlays: Boolean = true
)

@Serializable
data class MiscSettings(
    @SerialName("activityMode") var activityMode: DiscordActivityMode = DiscordActivityMode.TRUSTED_ONLY
)

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
