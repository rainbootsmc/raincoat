package dev.uten2c.raincoat

import kotlinx.serialization.cbor.Cbor

const val MINECRAFT = "minecraft"
const val MOD_ID = "raincoat"
val modelCbor = Cbor {
    ignoreUnknownKeys = true
}
