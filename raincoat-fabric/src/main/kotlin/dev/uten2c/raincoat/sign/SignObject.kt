package dev.uten2c.raincoat.sign

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

data class SignObject(
    val ids: List<Int>,
    val rawIds: List<String>,
    val replaceBlock: Block,
    val offset: Vec3d,
    val door: Pair<BlockPos, BlockPos>?,
    val isOldId: Boolean,
) {
    val itemStack: ItemStack by lazy {
        Items.RED_DYE.defaultStack.also { nbt ->
            nbt.orCreateNbt.putInt("CustomModelData", ids.first())
        }
    }
}
