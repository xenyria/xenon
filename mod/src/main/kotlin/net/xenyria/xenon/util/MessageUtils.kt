package net.xenyria.xenon.util

import net.minecraft.network.chat.Component
import net.xenyria.xenon.message.Message

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