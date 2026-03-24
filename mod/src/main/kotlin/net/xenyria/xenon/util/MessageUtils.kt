package net.xenyria.xenon.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.xenyria.xenon.message.Message
import java.awt.Color

private val gson = Gson()

private val errorComponent = Component.literal("<Error>").apply {
    style = style.withColor(Color.RED.rgb)
}

fun parseComponentFromJSON(jsonString: String): Component {
    return runCatching {
        val deserialized = ComponentSerialization.CODEC.decode(
            JsonOps.INSTANCE,
            gson.fromJson(jsonString, JsonElement::class.java)
        )
        if (deserialized.isError) return errorComponent
        deserialized.getOrThrow().first
    }.getOrDefault(errorComponent)
}

fun Message.toComponent(): Component {
    val components = components.map {
        val component = if (it.isTranslated) {
            Component.translatable(it.text)
        } else {
            Component.literal(it.text)
        }
        var style = component.style
        style = style.withColor(it.color.rgb)
        component.style = style
        component
    }
    if (components.isEmpty()) return Component.literal("")
    if (components.size == 1) return components[0]
    val root = components[0]

    val siblings = components.subList(1, components.size)
    if (siblings.isEmpty()) return root
    for (sibling in siblings) root.append(sibling)
    return root
}