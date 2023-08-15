package dev.uten2c.raincoat.mixin.block.entity;

import net.minecraft.block.entity.HangingSignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HangingSignBlockEntity.class)
public class MixinHangingSignBlockEntity {
    @Inject(method = "getMaxTextWidth", at = @At("HEAD"), cancellable = true)
    private void changeMaxWidth(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(300);
    }
}
