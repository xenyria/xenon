package net.xenyria.xenon.config

import net.fabricmc.loader.api.FabricLoader
import net.xenyria.xenon.LOGGER
import net.xenyria.xenon.xenon
import java.io.File
import java.nio.charset.StandardCharsets

object XenonClientConfig {

    private var _config: XenonConfig? = null

    private val configFile: File get() = FabricLoader.getInstance().configDir.resolve("xenon.json").toFile()

    @get:Synchronized
    val config: XenonConfig
        get() {
            var config = _config
            if (config == null) {
                val file = configFile
                if (!file.exists()) {
                    file.parentFile.mkdirs()
                    file.createNewFile()
                    file.writeText(XenonConfig.encode(XenonConfig()))
                }
                val configText = file.readText(StandardCharsets.UTF_8)
                config = runCatching {
                    XenonConfig.decode(configText)
                }.onFailure { ex ->
                    LOGGER.error("Failed to load Xenon config", ex)
                }.getOrDefault(XenonConfig())
                _config = config
            }
            return config
        }

    @Synchronized
    fun setDiscordActivityMode(enabled: RichPresenceMode) {
        mutateConfig { config.misc.activityMode = enabled }

        val session = xenon.getSessionOrNull() ?: return
        if (!session.canApplyActivity()) session.stopActivity()
    }

    @Synchronized
    fun setGizmosEnabled(enabled: Boolean) {
        mutateConfig { developer.enableGizmos = enabled }

        val forklift = xenon.getForkliftOrNull() ?: return
        if (!enabled) {
            if (forklift.editor.isActive) {
                forklift.editor.leaveEditMode()
                forklift.editor.leaveDragMode()
            }
        }
    }

    @Synchronized
    fun mutateConfig(block: XenonConfig.() -> Unit) {
        val copy = config
        block(copy)
        _config = copy
        saveConfig()
    }

    @Synchronized
    fun saveConfig() {
        val file = configFile
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val config = _config ?: return
        file.writeText(XenonConfig.encode(config))
    }

}