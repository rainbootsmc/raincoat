package dev.uten2c.raincoat.model

import kotlinx.serialization.Serializable
import net.minecraft.client.render.model.json.Transformation

@Serializable
data class Display(
    val rotation: Vec3 = rotationDefault,
    val translation: Vec3 = translationDefault,
    val scale: Vec3 = scaleDefault,
) {
    fun toMinecraft(): Transformation {
        return Transformation(rotation.toMinecraft(), translation.toMinecraft(), scale.toMinecraft())
    }

    companion object {
        val rotationDefault = Vec3.zero
        val translationDefault = Vec3.zero
        val scaleDefault = Vec3.one
        val zeroScale = Display(scale = Vec3(0f, 0f, 0f))

        @JvmField
        val IDENTITY = Display(Vec3.zero, Vec3.zero, Vec3.one)
    }
}
