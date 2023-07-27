package dev.uten2c.raincoat.sign

import net.minecraft.block.Block

data class SignObject(
    val ids: List<Int>,
    val replaceBlock: Block,
    val tableId: String?,
)
