package dev.uten2c.raincoat.debug

import net.minecraft.util.math.Vec3d

data class DebugShape(val min: Vec3d, val max: Vec3d, val color: UInt) {
    val alpha = (color shr 24 and 0xFFu).toInt() / 255f
    val red = (color shr 16 and 0xFFu).toInt() / 255f
    val green = (color shr 8 and 0xFFu).toInt() / 255f
    val blue = (color and 0xFFu).toInt() / 255f
}
