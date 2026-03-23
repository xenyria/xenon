package net.xenyria.xenon

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.network.chat.Component
import net.xenyria.xenon.config.CameraMode
import net.xenyria.xenon.config.RichPresenceMode
import net.xenyria.xenon.config.XenonClientConfig
import net.xenyria.xenon.config.XenonClientConfig.mutateConfig
import net.xenyria.xenon.config.XenonConfig

object Settings {

    val config: XenonConfig get() = XenonClientConfig.config

    private val gameplayCategory = ConfigCategory.createBuilder().name(Component.translatable("xenon_config_tab_gameplay"))
    private val miscCategory = ConfigCategory.createBuilder().name(Component.translatable("xenon_config_tab_misc"))
    private val developerCategory = ConfigCategory.createBuilder().name(Component.translatable("xenon_config_tab_developer"))

    private val gameplayOptionGroup = OptionGroup.createBuilder()
    private val miscOptionGroup = OptionGroup.createBuilder()
    private val developerOptionGroup = OptionGroup.createBuilder()

    private fun makeCheckbox(key: String, getter: () -> Boolean, setter: (Boolean) -> Unit): Option<Boolean> {
        return Option.createBuilder<Boolean>()
            .name(Component.translatable(key))
            .binding(
                true,
                { getter() },
                { setter(it) })
            .controller { option: Option<Boolean> -> TickBoxControllerBuilder.create(option) }
            .build()
    }

    fun start() {
        val builder = YetAnotherConfigLib.createBuilder()

        builder.title(Component.literal("Used for narration. Could be used to render a title in the future."))

        gameplayOptionGroup.option(
            Option.createBuilder<CameraMode>()
                .name(Component.translatable("xenon_config_gameplay_camera_control"))
                .binding(
                    CameraMode.CHANGE_AND_LOCK,
                    { config.camera.mode },
                    {
                        mutateConfig { config.camera.mode = it }
                    })
                .controller { option: Option<CameraMode> -> EnumControllerBuilder.create(option).enumClass(CameraMode::class.java) }
                .build()
        )
        miscOptionGroup.option(
            Option.createBuilder<RichPresenceMode>()
                .name(Component.translatable("xenon_discord_activity"))
                .binding(
                    RichPresenceMode.TRUSTED_ONLY,
                    { config.misc.discordActivityMode },
                    {
                        mutateConfig { XenonClientConfig.setDiscordRichPresence(it) }
                    })
                .controller { option: Option<RichPresenceMode> -> EnumControllerBuilder.create(option).enumClass(RichPresenceMode::class.java) }
                .build()
        )
        developerOptionGroup.option(
            makeCheckbox("xenon_forklift", { config.developer.enableForklift }, { config.developer.enableForklift = it })
        )

        gameplayCategory.group(gameplayOptionGroup.build())

        builder.categories(listOf(gameplayCategory.build(), miscCategory.build(), developerCategory.build()))
        builder.build()

    }

}