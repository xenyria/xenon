package net.xenyria.xenon.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.xenyria.xenon.GameEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public final class CameraModeMixin {

    @Redirect(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z",
                    ordinal = 0 // adjust this!
            )
    )
    private boolean preventPerspectiveToggle(KeyMapping instance) {
        if (GameEvents.INSTANCE.onTogglePerspectiveKeybind()) {
            return false; // Pretend key was not pressed
        }

        return instance.consumeClick();
    }
}
