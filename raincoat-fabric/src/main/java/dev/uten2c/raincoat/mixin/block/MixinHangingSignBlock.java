package dev.uten2c.raincoat.mixin.block;

import dev.uten2c.raincoat.sign.SignListener;
import net.minecraft.block.*;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HangingSignBlock.class)
public abstract class MixinHangingSignBlock extends AbstractSignBlock {
    protected MixinHangingSignBlock(Settings settings, WoodType type) {
        super(settings, type);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void getOutlineShape$(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        final var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignBlockEntity signBlockEntity && SignListener.getCachedSignObject(signBlockEntity) != null) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}
