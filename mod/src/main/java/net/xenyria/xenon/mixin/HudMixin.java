package net.xenyria.xenon.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = Integer.MIN_VALUE)
public final class HudMixin {

    @ModifyArg(
            method = "renderItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            ),
            index = 5
    )
    private int swapHotbarSelectorIconHeight(int height) {
        return height + net.xenyria.xenon.util.MixinUtilsKt.getCurrentHudOffset();
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    private void startMainHudTranslate(GuiGraphics graphics, DeltaTracker deltaTime, CallbackInfo callbackInfo) {
        net.xenyria.xenon.util.MixinUtilsKt.shift(graphics.pose(), net.xenyria.xenon.util.MixinUtilsKt.getHudOffsetVector());
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("TAIL"))
    private void endMainHudTranslate(GuiGraphics graphics, DeltaTracker deltaTime, CallbackInfo callbackInfo) {
        net.xenyria.xenon.util.MixinUtilsKt.unshift(graphics.pose());
    }


}
