package dev.uten2c.raincoat.model

import kotlinx.serialization.SerialName
import net.minecraft.util.math.Direction

@Suppress("unused")
enum class Direction {
    @SerialName("down")
    DOWN,

    @SerialName("up")
    UP,

    @SerialName("north")
    NORTH,

    @SerialName("south")
    SOUTH,

    @SerialName("west")
    WEST,

    @SerialName("east")
    EAST,
    ;

    fun toMinecraft(): Direction {
        return Direction.byId(this.ordinal)
    }
}
