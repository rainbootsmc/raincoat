package dev.uten2c.raincoat.sign

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

data class SignObject(
    val ids: List<Int>,
    val replaceBlock: Block,
    val tableId: String?,
) {
    val itemStack: ItemStack by lazy {
        Items.RED_DYE.defaultStack.also { nbt ->
            nbt.orCreateNbt.putInt("CustomModelData", ids.first())
        }
    }
}
