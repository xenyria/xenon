package net.xenyria.xenon.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

class XenonModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return { parentScreen -> Settings.create().generateScreen(parentScreen) }
    }
}