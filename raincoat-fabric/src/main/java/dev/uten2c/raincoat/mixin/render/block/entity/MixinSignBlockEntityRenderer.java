package dev.uten2c.raincoat.mixin.render.block.entity;

import dev.uten2c.raincoat.sign.SignListener;
import dev.uten2c.raincoat.sign.SignObjectUtils;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntityRenderer.class)
public class MixinSignBlockEntityRenderer {
    @Unique
    private ItemRenderer itemRenderer$raincoat;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(BlockEntityRendererFactory.Context ctx, CallbackInfo ci) {
        itemRenderer$raincoat = ctx.getItemRenderer();
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/block/BlockState;Lnet/minecraft/block/AbstractSignBlock;Lnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model;)V", at = @At("HEAD"), cancellable = true)
    private void render(SignBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BlockState state, AbstractSignBlock block, WoodType woodType, Model model, CallbackInfo ci) {
        final var signObject = SignListener.getCachedSignObject(entity);
        if (signObject == null) {
            return;
        }
        ci.cancel();

        final var yaw = SignObjectUtils.getYaw(entity.getCachedState());
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw + 180));
        itemRenderer$raincoat.renderItem(signObject.getItemStack(), ModelTransformationMode.HEAD, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}
