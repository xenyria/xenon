package net.xenyria.xenon.mixin;

import net.minecraft.client.Minecraft;
import net.xenyria.xenon.GameEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class GameTickMixin {

    @Inject(at = @At("HEAD"), method = "runTick(Z)V")
    private void runTick(boolean value, CallbackInfo info) {
        GameEvents.INSTANCE.onTick();
    }

}
