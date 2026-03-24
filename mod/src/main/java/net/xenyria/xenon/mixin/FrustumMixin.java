package net.xenyria.xenon.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import net.xenyria.xenon.Xenon;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public final class FrustumMixin {

    @Inject(method = "prepareCullFrustum", at = @At("RETURN"))
    private void captureFrustum(
            Matrix4f matrix4f,
            Matrix4f matrix4f2,
            Vec3 vec3,
            CallbackInfoReturnable<Frustum> cir
    ) {
        Frustum frustum = cir.getReturnValue();
        // Store it somewhere
        Xenon.Companion.getInstance().getClient().setFrustum(frustum);
    }
}
