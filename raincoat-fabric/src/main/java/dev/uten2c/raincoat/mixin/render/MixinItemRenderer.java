package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.model.GunBakedModel;
import dev.uten2c.raincoat.model.GunUnbakedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void changeGunModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        final var model = cir.getReturnValue();
        if (model instanceof GunBakedModel rainModel) {
            final var isFirstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
            final var isFirstPersonModel = MinecraftClient.getInstance().cameraEntity == entity && isFirstPerson;
            final var bakedModel = GunUnbakedModel.getOrCreateModel(isFirstPersonModel, stack, rainModel);
            if (bakedModel != null) {
                cir.setReturnValue(bakedModel);
            }
        }
    }
}
