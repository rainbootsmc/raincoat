package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.util.GunState;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    private long lastNoBobbingTime = 0L;
    private float lastScale = 0L;
    private static final int MARGIN_MILLIS = 50;

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow
    @Final
    MinecraftClient client;

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void bobView(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        var cameraEntity = client.getCameraEntity();
        if (States.isOnServer() && cameraEntity instanceof PlayerEntity player && StackUtils.isGun(player.getMainHandStack())) {
            var mainHandStack = player.getMainHandStack();
            var gunState = StackUtils.getGunState(mainHandStack);

            var gunModelState = StackUtils.getGunModelState(mainHandStack);
            var targetScale = StackUtils.getGunModelScale(gunModelState);

            if (gunState == GunState.ADS || System.currentTimeMillis() - lastNoBobbingTime <= MARGIN_MILLIS) {
                targetScale *= 0.01;
                if (gunState == GunState.ADS) {
                    lastNoBobbingTime = System.currentTimeMillis();
                }
            }
            var scale = MathHelper.lerp(tickDelta, lastScale, targetScale);
            lastScale = scale;

            float f = (player.horizontalSpeed - player.prevHorizontalSpeed);
            float g = -(player.horizontalSpeed + f * tickDelta);
            float h = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance) * scale;
            matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5f, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2f) * h) * 5.0f));
            return;
        }
        bobView(matrices, tickDelta);
    }
}
