package dev.uten2c.raincoat.sign

import net.minecraft.block.BlockState
import net.minecraft.block.SignBlock
import net.minecraft.block.WallSignBlock
import net.minecraft.util.math.RotationPropertyHelper

object SignObjectUtils {
    @JvmStatic
    fun getYaw(state: BlockState): Float {
        return if (state.contains(WallSignBlock.FACING)) {
            val direction = state.get(WallSignBlock.FACING)
            val rotation = RotationPropertyHelper.fromDirection(direction)
            RotationPropertyHelper.toDegrees(rotation)
        } else {
            RotationPropertyHelper.toDegrees(state.get(SignBlock.ROTATION))
        }
    }
}
