package dev.uten2c.raincoat.mixin.network;

import com.mojang.authlib.GameProfile;
import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void dropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        var stack = getInventory().getMainHandStack();
        if (States.isOnServer() && StackUtils.canNotDrop(stack)) {
            cir.setReturnValue(false);
        }
    }
}
