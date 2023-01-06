package dev.uten2c.raincoat.mixin.render;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class MixinHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
    public MixinHeldItemFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Redirect(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void renderGun(HeldItemRenderer instance, LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (States.isOnServer()) {
            var originalStack = stack.copy();
            var thirdPersonStack = StackUtils.getThirdPersonStack(originalStack);
            if (!thirdPersonStack.isEmpty()) {
                instance.renderItem(entity, thirdPersonStack, renderMode, leftHanded, matrices, vertexConsumers, light);
                return;
            }
        }
        instance.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, light);
    }
}
