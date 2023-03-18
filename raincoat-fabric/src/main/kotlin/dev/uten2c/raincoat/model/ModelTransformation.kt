package dev.uten2c.raincoat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.render.model.json.ModelTransformation

@Serializable
data class ModelTransformation(
    @SerialName("thirdperson_lefthand") val thirdPersonLeftHand: Display = Display.IDENTITY,
    @SerialName("thirdperson_righthand") val thirdPersonRightHand: Display = Display.IDENTITY,
    @SerialName("firstperson_lefthand") val firstPersonLeftHand: Display = Display.IDENTITY,
    @SerialName("firstperson_righthand") val firstPersonRightHand: Display = Display.IDENTITY,
    val head: Display = Display.IDENTITY,
    val gui: Display = Display.IDENTITY,
    val ground: Display = Display.IDENTITY,
    val fixed: Display = Display.IDENTITY,
) {
    fun toMinecraft(): ModelTransformation {
        return ModelTransformation(
            thirdPersonLeftHand.toMinecraft(),
            thirdPersonRightHand.toMinecraft(),
            firstPersonLeftHand.toMinecraft(),
            firstPersonRightHand.toMinecraft(),
            head.toMinecraft(),
            gui.toMinecraft(),
            ground.toMinecraft(),
            fixed.toMinecraft(),
        )
    }
}
