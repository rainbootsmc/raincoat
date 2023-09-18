package dev.uten2c.raincoat.mixin.render.debug;

import dev.uten2c.raincoat.debug.DebugShape;
import dev.uten2c.raincoat.debug.DebugShapes;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderDebugShape(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        for (DebugShape debugShape : DebugShapes.getShapes()) {
            debugShape.draw(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        }
    }
}
