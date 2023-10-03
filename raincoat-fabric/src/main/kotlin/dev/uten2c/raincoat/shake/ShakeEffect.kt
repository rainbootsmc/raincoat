package dev.uten2c.raincoat.shake

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.minecraft.util.math.MathHelper
import kotlin.time.Duration

data class ShakeEffect(val startTime: Instant, val duration: Duration, val strength: Float) {
    val progress: Float
        get() = MathHelper.clamp(((Clock.System.now() - startTime) / duration).toFloat(), 0f, 1f)

    @JvmName("shouldPlay")
    fun shouldPlay(): Boolean {
        return strength > 0 && Clock.System.now() - startTime < duration
    }
}
