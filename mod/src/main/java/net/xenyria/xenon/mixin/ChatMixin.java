package net.xenyria.xenon.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.xenyria.xenon.util.MixinUtilsKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xenyria.xenon.util.MixinUtilsKt.getChatOffsetVector;
import static net.xenyria.xenon.util.MixinUtilsKt.getCurrentChatOffset;

/**
 * Mixin for shifting the chat up when the player is in editing mode.
 */
@Mixin(value = ChatScreen.class, priority = Integer.MIN_VALUE)
public final class ChatMixin {

    /// Shift the chat up by the current offset after it has been rendered
    @Inject(method = "render", at = @At("HEAD"))
    private void shiftChat(GuiGraphics graphics, int x, int y, float delta, CallbackInfo callbackInfo) {
        MixinUtilsKt.shift(graphics.pose(), getChatOffsetVector());
    }

    // Reset render state after rendering the chat
    @Inject(method = "render", at = @At("TAIL"))
    private void unshiftChat(GuiGraphics graphics, int x, int y, float delta, CallbackInfo callbackInfo) {
        MixinUtilsKt.unshift(graphics.pose());
    }

    // Adjust click position during edit mode to account the vertical shift of the chat
    @ModifyVariable(method = "mouseClicked", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private MouseButtonEvent adjustChatMouseClick(MouseButtonEvent event) {
        return new MouseButtonEvent(
                event.x(),
                event.y() - getCurrentChatOffset(),
                event.buttonInfo()
        );
    }
}
