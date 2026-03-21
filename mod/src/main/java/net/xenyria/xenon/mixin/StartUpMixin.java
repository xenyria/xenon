package net.xenyria.xenon.mixin;

import net.minecraft.client.Minecraft;
import net.xenyria.xenon.Xenon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class StartUpMixin {

    @Inject(at = @At("HEAD"), method = "run")
    public void run(CallbackInfo info) {
        Xenon.Companion.getInstance().initialize(Minecraft.getInstance());
    }

}
