package dev.uten2c.raincoat.util

@JvmRecord
data class PacketId(val value: String) {
    override fun toString(): String {
        return "raincoat:$value"
    }
}
