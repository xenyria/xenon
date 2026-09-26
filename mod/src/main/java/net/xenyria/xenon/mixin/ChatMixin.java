package net.xenyria.xenon.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.xenyria.xenon.util.MixinUtilsKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xenyria.xenon.util.MixinUtilsKt.*;

/**
 * Mixin for shifting the chat up when the player is in editing mode.
 */
@SuppressWarnings("DataFlowIssue")
@Mixin(value = ChatScreen.class, priority = Integer.MIN_VALUE)
public final class ChatMixin {

    private final static int CHAT_INPUT_BASE_X = 4;
    private final static int CHAT_INPUT_BASE_Y = 12;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @Shadow
    protected EditBox input;

    // Shift the chat up by the current offset during extraction
    @Inject(method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V"
            )
    )
    private void shiftChat(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float f,
            CallbackInfo callbackInfo
    ) {
        MixinUtilsKt.shift(graphics.pose(), getChatOffsetVector());
    }

    // Reset render state after rendering the chat
    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
                    shift = At.Shift.AFTER
            )
    )
    private void unshiftChat(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float f, CallbackInfo callbackInfo) {
        MixinUtilsKt.unshift(graphics.pose());
    }

    // New in 26.3: We also need to handle the chat input box.
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void fixChatInput(
            GuiGraphicsExtractor graphics,
            int mouseX, int mouseY, float f,
            CallbackInfo callbackInfo
    ) {
        ChatScreen self = (ChatScreen) (Object) this;
        if (shouldShift()) {
            // I don't know if it'll break compatibility with other mods.
            // Since Forklift is niche anyway, it's probably best to only ever run this when it's strictly necessary.
            input.setPosition(
                    CHAT_INPUT_BASE_X,
                    (self.height - CHAT_INPUT_BASE_Y) + getCurrentChatOffset()
            );
        }
    }

    @ModifyArg(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V",
                    ordinal = 0
            ),
            index = 1
    )
    private int fixChatBackgroundMinY(int minY) {
        return minY + getCurrentChatOffset();
    }

    @ModifyArg(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V",
                    ordinal = 0
            ),
            index = 3
    )
    private int fixChatBackgroundMaxY(int maxY) {
        return maxY + getCurrentChatOffset();
    }

    // Adjust click position during edit mode to account the vertical shift of the chat
    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/MouseButtonEvent;button()I"))
    private int redirectChatMouseClickButton(MouseButtonEvent instance) {
        if (!shouldShift()) return instance.button();
        return new MouseButtonEvent(
                instance.x(),
                instance.y() - getCurrentChatOffset(),
                instance.buttonInfo()
        ).button();
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/MouseButtonEvent;y()D"))
    private double redirectChatMouseClickY(MouseButtonEvent instance) {
        if (!shouldShift()) return instance.y();
        return instance.y() - getCurrentChatOffset();
    }

}
