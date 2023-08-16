package dev.uten2c.raincoat.sign

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.Vec3d

data class SignObject(
    val ids: List<Int>,
    val replaceBlock: Block,
    val tableId: String?,
    val bbStart: Vec3d,
    val bbEnd: Vec3d,
    val bbShow: Boolean,
    val offset: Vec3d,
) {
    val itemStack: ItemStack by lazy {
        Items.RED_DYE.defaultStack.also { nbt ->
            nbt.orCreateNbt.putInt("CustomModelData", ids.first())
        }
    }
}
