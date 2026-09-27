package net.xenyria.xenon.mixin;

import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.xenyria.xenon.util.MixinUtilsKt.getCurrentChatOffset;

@SuppressWarnings("unused")
@Mixin(value = CommandSuggestions.class, priority = Integer.MIN_VALUE)
public class CommandSuggestionMixin {

    @Redirect(
            method = "showSuggestions",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screens/Screen;height:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int redirectSuggestionY(Screen instance) {
        return instance.height + getCurrentChatOffset();
    }

    @Redirect(
            method = "extractUsage",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screens/Screen;height:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int adjustUsageInfoY(Screen instance) {
        return instance.height + getCurrentChatOffset();
    }
}
