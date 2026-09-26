package net.xenyria.xenon.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Hud.class, priority = Integer.MIN_VALUE)
public final class HudMixin {

    @ModifyArg(
            method = "extractItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            ),
            index = 5
    )
    private int shiftItemHotbar(int height) {
        return net.xenyria.xenon.util.MixinUtilsKt.getCurrentHudOffset();
    }

    @Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
    private void startMainHudTranslate(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTime,
            CallbackInfo callbackInfo
    ) {
        net.xenyria.xenon.util.MixinUtilsKt.shift(graphics.pose(), net.xenyria.xenon.util.MixinUtilsKt.getHudOffsetVector());
    }

    @Inject(method = "extractHotbarAndDecorations", at = @At("TAIL"))
    private void endMainHudTranslate(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTime,
            CallbackInfo callbackInfo
    ) {
        net.xenyria.xenon.util.MixinUtilsKt.unshift(graphics.pose());
    }


}
