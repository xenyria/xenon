package net.xenyria.xenon.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import net.xenyria.xenon.GameEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {
    @Inject(at = @At("HEAD"), method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", cancellable = true)
    private void onKey(long window, int action, KeyEvent event, CallbackInfo info) {
        GameEvents.INSTANCE.onKeyPress(window, action, event, info);
    }
}