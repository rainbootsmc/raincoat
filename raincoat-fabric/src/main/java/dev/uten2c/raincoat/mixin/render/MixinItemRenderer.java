package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.model.GunBakedModel;
import dev.uten2c.raincoat.model.GunUnbakedModel;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @ModifyVariable(method = "innerRenderInGui(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private ItemStack renderItem(ItemStack value) {
        if (!States.isOnServer()) {
            return value;
        }
        if (value.isEmpty()) {
            return value;
        }
        var guiStack = StackUtils.getGuiStack(value);
        if (guiStack.isEmpty()) {
            return value;
        }
        return guiStack;
    }

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void changeGunModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        final var model = cir.getReturnValue();
        if (model instanceof GunBakedModel rainModel) {
            final var bakedModel = GunUnbakedModel.getOrCreateModel(stack, rainModel);
            if (bakedModel != null) {
                cir.setReturnValue(bakedModel);
            }
        }
    }
}
