package net.xenyria.xenon.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import net.xenyria.xenon.Xenon;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mixin(Camera.class)
public final class FrustumMixin {

    @Inject(method = "prepareCullFrustum", at = @At("RETURN"))
    private void captureFrustum(
            Matrix4fc matrix4f, Matrix4f matrix4f2, Vec3 vec3,
            CallbackInfo info
    ) {
        Camera self = (Camera) (Object) this;
        Frustum frustum = self.getCullFrustum();
        // Store it somewhere (used for performing frustum culling with debug rendering)
        Xenon.Companion.getInstance().getClient().setFrustum(frustum);
    }
}
