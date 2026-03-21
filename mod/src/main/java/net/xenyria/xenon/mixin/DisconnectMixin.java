package net.xenyria.xenon.mixin;

import net.minecraft.client.Minecraft;
import net.xenyria.xenon.GameEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class DisconnectMixin {

    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;ZZ)V")
    public void handleDisconnect(CallbackInfo info) {
        GameEvents.INSTANCE.onDisconnect();
    }
}
