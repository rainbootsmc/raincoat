package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.shake.ShakeEffectUtils;
import dev.uten2c.raincoat.util.GunState;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Unique
    private float worldBobbingMultiplier = 0f;

    @Unique
    private float handBobbingMultiplier = 0f;

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow
    @Final
    MinecraftClient client;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", shift = At.Shift.AFTER))
    private void shakeEffect(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        final var shakeEffect = States.getShakeEffect();
        if (shakeEffect == null || !shakeEffect.shouldPlay()) {
            return;
        }
        final var strength = 1f - (float) Math.pow(shakeEffect.getProgress(), 3.0);
        ShakeEffectUtils.shake(matrices, MathHelper.clamp(strength, 0f, 1f) * shakeEffect.getStrength());
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void bobView1(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        var cameraEntity = client.getCameraEntity();
        if (!States.isOnServer() || !(cameraEntity instanceof AbstractClientPlayerEntity player) || !StackUtils.isGun(player.getMainHandStack())) {
            bobView(matrices, tickDelta);
            return;
        }

        final var mainHandStack = player.getMainHandStack();
        final var gunState = StackUtils.getGunState(mainHandStack);

        final var lastFrameDuration = this.client.getLastFrameDuration();
        if (gunState == GunState.ADS) {
            worldBobbingMultiplier -= 0.1f * lastFrameDuration;
        } else {
            worldBobbingMultiplier += 0.1f * lastFrameDuration;
        }
        worldBobbingMultiplier = MathHelper.clamp(worldBobbingMultiplier, 0f, 1f);

        raincoat$bobView(matrices, tickDelta, worldBobbingMultiplier);
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void bobView2(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        var cameraEntity = client.getCameraEntity();
        if (!States.isOnServer() || !(cameraEntity instanceof AbstractClientPlayerEntity player) || !StackUtils.isGun(player.getMainHandStack())) {
            bobView(matrices, tickDelta);
            return;
        }

        final var mainHandStack = player.getMainHandStack();
        final var gunState = StackUtils.getGunState(mainHandStack);

        final var isFirstPerson = client.options.getPerspective().isFirstPerson();
        final var gunModelState = StackUtils.getGunModelState(isFirstPerson, mainHandStack);
        final var targetScale = StackUtils.getGunModelScale(gunModelState);
        final var minMultiplier = player.getFovMultiplier() * 0.0075f;

        final var lastFrameDuration = this.client.getLastFrameDuration();
        if (gunState == GunState.ADS) {
            handBobbingMultiplier = 0f;
        } else {
            handBobbingMultiplier += 0.1f * targetScale * lastFrameDuration;
        }
        handBobbingMultiplier = MathHelper.clamp(handBobbingMultiplier, minMultiplier, targetScale);

        raincoat$bobView(matrices, tickDelta, handBobbingMultiplier);
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    private Object forceEnableIfItemIsGun(SimpleOption<Boolean> instance) {
        var cameraEntity = client.getCameraEntity();
        if (!States.isOnServer() || !(cameraEntity instanceof AbstractClientPlayerEntity player) || !StackUtils.isGun(player.getMainHandStack())) {
            return instance.getValue();
        }
        return true;
    }

    @Unique
    private void raincoat$bobView(MatrixStack matrices, float tickDelta, float multiplier) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity player)) {
            return;
        }
        float f = (player.horizontalSpeed - player.prevHorizontalSpeed);
        float g = -(player.horizontalSpeed + f * tickDelta);
        float h = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance) * multiplier;
        matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5f, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2f) * h) * 5.0f));
    }
}
