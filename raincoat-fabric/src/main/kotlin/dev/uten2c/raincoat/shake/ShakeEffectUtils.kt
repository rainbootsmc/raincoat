package dev.uten2c.raincoat.shake

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import kotlin.random.Random

object ShakeEffectUtils {
    @JvmStatic
    fun shake(matrices: MatrixStack, strength: Float) {
        val r1 = randomFloat() * strength
        val r2 = randomFloat() * strength
        matrices.translate(r1.toDouble() / 4, r2.toDouble() / 8, 0.0)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(r1))
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(r2 * 2))
    }

    private fun randomFloat(): Float {
        return Random.nextFloat() - 0.5f
    }
}
