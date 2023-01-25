package dev.uten2c.raincoat.mixin.network;

import com.mojang.authlib.GameProfile;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {
    public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getActiveItem()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void zoom(CallbackInfoReturnable<Float> cir) {
        var stack = getMainHandStack();
        var zoomLevel = StackUtils.getZoomLevel(stack);
        if (zoomLevel != null) {
            switch (zoomLevel) {
                case ZOOM_1X -> cir.setReturnValue(0.9f);
                case ZOOM_2X -> cir.setReturnValue(0.5f);
                case ZOOM_4X -> cir.setReturnValue(0.25f);
            }
        }
    }
}
