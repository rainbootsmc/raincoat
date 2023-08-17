package dev.uten2c.raincoat

import kotlinx.serialization.cbor.Cbor
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.text.Text

const val MINECRAFT = "minecraft"
const val MOD_ID = "raincoat"
val modelCbor = Cbor {
    ignoreUnknownKeys = true
}

var shouldUpdateCreativeTab = false
val fieldObjectItemGroupBarrier: ItemGroup = FabricItemGroup.builder()
    .icon { Items.BARRIER.defaultStack }
    .displayName(Text.of("Field Objects (Barrier)"))
    .build()

val fieldObjectItemGroupAir: ItemGroup = FabricItemGroup.builder()
    .icon { Items.GLASS.defaultStack }
    .displayName(Text.of("Field Objects (Air)"))
    .build()
