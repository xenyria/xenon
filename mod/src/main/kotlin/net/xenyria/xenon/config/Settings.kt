package net.xenyria.xenon.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.network.chat.Component

object Settings {

    val config: XenonConfig get() = XenonClientConfig.config

    fun create(): YetAnotherConfigLib {
        return Builder().create()
    }

    class Builder {

        private val gameplayCategory = ConfigCategory.createBuilder().name(Component.translatable("xenon_config_tab_gameplay"))
        private val miscCategory = ConfigCategory.createBuilder().name(Component.translatable("xenon_config_tab_misc"))
        private val developerCategory = ConfigCategory.createBuilder().name(Component.translatable("xenon_config_tab_developer"))

        private val gameplayOptionGroup = OptionGroup.createBuilder()
        private val miscOptionGroup = OptionGroup.createBuilder()
        private val developerOptionGroup = OptionGroup.createBuilder()

        private fun makeCheckbox(key: String, getter: () -> Boolean, setter: XenonConfig.(value: Boolean) -> Unit): Option<Boolean> {
            return Option.createBuilder<Boolean>()
                .name(Component.translatable(key))
                .binding(
                    true,
                    { getter() },
                    {
                        XenonClientConfig.mutateConfig {
                            setter(it)
                        }
                    })
                .controller { option: Option<Boolean> -> TickBoxControllerBuilder.create(option) }
                .build()
        }

        fun create(): YetAnotherConfigLib {
            val builder = YetAnotherConfigLib.createBuilder()

            builder.title(Component.literal("Used for narration. Could be used to render a title in the future."))

            gameplayOptionGroup.option(
                Option.createBuilder<CameraMode>()
                    .name(Component.translatable("xenon_config_gameplay_camera_control"))
                    .binding(
                        CameraMode.CHANGE_AND_LOCK,
                        { config.camera.mode },
                        {
                            XenonClientConfig.mutateConfig { config.camera.mode = it }
                        })
                    .controller { option: Option<CameraMode> ->
                        EnumControllerBuilder.create(option)
                            .formatValue { Component.translatable(it.key) }
                            .enumClass(CameraMode::class.java)
                    }
                    .build()
            )
            miscOptionGroup.option(
                Option.createBuilder<RichPresenceMode>()
                    .name(Component.translatable("xenon_discord_activity"))
                    .binding(
                        RichPresenceMode.TRUSTED_ONLY,
                        { config.misc.activityMode },
                        {
                            XenonClientConfig.mutateConfig { XenonClientConfig.setDiscordActivityMode(it) }
                        })
                    .controller { option: Option<RichPresenceMode> ->
                        EnumControllerBuilder.create(option)
                            .formatValue { Component.translatable(it.key) }
                            .enumClass(RichPresenceMode::class.java)
                    }
                    .build()
            )
            developerOptionGroup.option(
                makeCheckbox("xenon_forklift", { config.developer.enableGizmos }, { XenonClientConfig.setGizmosEnabled(it) })
            )
            developerOptionGroup.option(
                makeCheckbox("xenon_debug_shapes", { config.developer.enableShapes }, { config.developer.enableShapes = it })
            )
            developerOptionGroup.option(
                makeCheckbox("xenon_debug_overlays", { config.developer.enableOverlays }, { config.developer.enableOverlays = it })
            )

            gameplayCategory.group(gameplayOptionGroup.build())
            miscCategory.group(miscOptionGroup.build())
            developerCategory.group(developerOptionGroup.build())

            builder.categories(listOf(gameplayCategory.build(), miscCategory.build(), developerCategory.build()))
            return builder.build()
        }
    }

}