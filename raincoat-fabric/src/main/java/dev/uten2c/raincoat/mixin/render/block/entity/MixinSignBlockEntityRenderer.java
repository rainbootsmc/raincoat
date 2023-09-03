package dev.uten2c.raincoat.mixin.render.block.entity;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.sign.FieldObjectModelMetadata;
import dev.uten2c.raincoat.sign.SignListener;
import dev.uten2c.raincoat.sign.SignObjectUtils;
import dev.uten2c.raincoat.util.FieldObjectRenderUtils;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaterniond;
import org.joml.Vector3d;
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

        final var rotation = SignObjectUtils.getYaw(entity.getCachedState()) * -1 + 180;
        final var offset = signObject.getOffset();
        final var localOffset = new Vector3d(offset.x, offset.y, offset.z);
        final var quaternion = new Quaterniond();
        quaternion.rotateY(Math.toRadians(rotation));
        quaternion.transform(localOffset);

        matrices.push();
        matrices.translate(localOffset.x + 0.5f, localOffset.y + 0.5f, localOffset.z + 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        itemRenderer$raincoat.renderItem(signObject.getItemStack(), ModelTransformationMode.HEAD, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();

//        if (signObject.isOldId()) {
//            matrices.push();
//            matrices.translate(0.5, 0.5, 0.5);
//            final var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
//            WorldRenderer.drawBox(
//                    matrices, vertexConsumer,
//                    -0.5, -0.5, -0.5,
//                    0.5, 0.5, 0.5,
//                    1f, 0f, 0f, 1f,
//                    1f, 1f, 1f
//            );
//            matrices.pop();
//        }

        if (!States.getShowDebugShape()) {
            return;
        }
        final var cameraEntity = MinecraftClient.getInstance().cameraEntity;
        if (cameraEntity == null) {
            return;
        }
        final var maxDistance = 64 * 16;
        final var distance = cameraEntity.squaredDistanceTo(entity.getPos().toCenterPos());
        if (distance > maxDistance) {
            return;
        }

        final var door = signObject.getDoor();
        if (door != null) {
            matrices.push();
            matrices.translate(0.5, 0.5, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            final var start = door.getFirst();
            final var end = door.getSecond();
            final var x1 = Math.min(start.getX(), end.getX()) - 0.49;
            final var y1 = Math.min(start.getY(), end.getY()) - 0.49;
            final var z1 = Math.min(start.getZ(), end.getZ()) - 0.49;
            final var x2 = Math.max(start.getX(), end.getX()) + 0.49;
            final var y2 = Math.max(start.getY(), end.getY()) + 0.49;
            final var z2 = Math.max(start.getZ(), end.getZ()) + 0.49;
            final var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
            WorldRenderer.drawBox(
                    matrices, vertexConsumer,
                    x1, y1, z1,
                    x2, y2, z2,
                    1f, 1f, 0.5f, 0.5f,
                    0f, 0f, 0f
            );
            matrices.pop();
        }

        FieldObjectRenderUtils.drawSignPosCube(matrices, vertexConsumers, rotation);

        final var metadata = FieldObjectModelMetadata.getOrDefault(signObject.getRawIds().get(0));
//        FieldObjectRenderUtils.drawModelBasedOutline(matrices, vertexConsumers, rotation, metadata.getDisplay(), signObject.getOffset(), metadata.getCollision(), 0xffffffff);
//        FieldObjectRenderUtils.drawModelBasedOutline(matrices, vertexConsumers, rotation, metadata.getDisplay(), signObject.getOffset(), metadata.getInteraction(), 0xff4444ff);
    }
}
