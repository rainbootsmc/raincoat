package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.util.GunState;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void adsPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        final var stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            return;
        }
        final var gunState = StackUtils.getGunState(stack);
        if (gunState == GunState.ADS) {
            cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
