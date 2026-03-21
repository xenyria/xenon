package net.xenyria.xenon.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.xenyria.xenon.GameEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public final class MouseMixin {
    //private void onButton(long l, MouseButtonInfo mouseButtonInfo, @MouseButtonInfo.Action int i) {

    @Inject(at = @At("HEAD"), method = "onButton(JLnet/minecraft/client/input/MouseButtonInfo;I)V", cancellable = true)
    public void handleMouseButtonInteraction(long l, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo info) {
        GameEvents.INSTANCE.onMouseButton(l, mouseButtonInfo, action, info);
    }

    @Inject(at = @At("HEAD"), method = "onMove(JDD)V", cancellable = true)
    public void handleCursorPos(long window, double x, double y, CallbackInfo info) {
        GameEvents.INSTANCE.onMouseMove(window, x, y, info);
    }

}
