package dev.uten2c.raincoat.mixin.render.item;

import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ItemStack mainHand;

    @Unique
    private float gunEquipProgress = 0f;

    @Unique
    private float prevGunEquipProgress = 0f;

    @Unique
    private ItemStack lastStack = ItemStack.EMPTY;

    @Inject(method = "updateHeldItems", at = @At("HEAD"), cancellable = true)
    private void updateGunEquipProgress(CallbackInfo ci) {
        final var player = this.client.player;
        assert player != null;
        final var itemStack = player.getMainHandStack();
        if (!StackUtils.isGun(itemStack)) {
            prevGunEquipProgress = 0f;
            gunEquipProgress = 0f;
            lastStack = ItemStack.EMPTY;
            return;
        }

        prevGunEquipProgress = gunEquipProgress;

        if (StackUtils.isGun(lastStack) && !ItemStack.areEqual(StackUtils.getThirdPersonStack(lastStack), StackUtils.getThirdPersonStack(itemStack))) {
            gunEquipProgress = 0f;
        }
        lastStack = itemStack;

        final var duration = StackUtils.getEquipDuration(itemStack);
        final var amount = 1000f / duration / 20f;
        gunEquipProgress = MathHelper.clamp(gunEquipProgress + amount, 0f, 1f);

        if (gunEquipProgress > 0.1f) {
            mainHand = itemStack;
        }

        ci.cancel();
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"))
    private void applyGunEquipOffset(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!StackUtils.isGun(item)) {
            return;
        }
        final var lerp = MathHelper.lerp(tickDelta, prevGunEquipProgress, gunEquipProgress);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation((float) Math.toRadians(45f) * (1f - lerp)));
        matrices.translate(0f, (-1f + lerp) * 0.5, 0f);
    }
}
