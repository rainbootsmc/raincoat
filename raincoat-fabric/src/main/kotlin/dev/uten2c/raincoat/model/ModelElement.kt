package dev.uten2c.raincoat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.render.model.json.ModelElement
import net.minecraft.client.render.model.json.ModelElementFace
import net.minecraft.client.render.model.json.ModelElementTexture
import net.minecraft.client.render.model.json.ModelRotation

@Serializable
data class ModelElement @JvmOverloads constructor(
    val from: Vec3 = Vec3.zero,
    val to: Vec3 = Vec3.zero,
    val faces: Map<Direction, Face>,
    val rotation: Rotation? = null,
    val shade: Boolean = false,
) {
    fun toMinecraft(): ModelElement {
        return ModelElement(
            from.toMinecraft(),
            to.toMinecraft(),
            faces.map { it.key.toMinecraft() to it.value.toMinecraft() }.toMap(),
            rotation?.toMinecraft(),
            shade,
        )
    }

    @Serializable
    data class Rotation(
        val angle: Float,
        val axis: Axis,
        val origin: Vec3,
        val rescale: Boolean = false,
    ) {
        fun toMinecraft(): ModelRotation {
            return ModelRotation(origin.toMinecraft(), axis.toMinecraft(), angle, rescale)
        }

        @Serializable
        @Suppress("unused")
        enum class Axis {
            @SerialName("x")
            X,

            @SerialName("y")
            Y,

            @SerialName("z")
            Z,
            ;

            fun toMinecraft(): net.minecraft.util.math.Direction.Axis {
                return net.minecraft.util.math.Direction.Axis.values()[ordinal]
            }
        }
    }

    @Serializable
    data class Face(
        @SerialName("cullface") val cullFace: Direction? = null,
        @SerialName("tintindex") val tintIndex: Int = -1,
        val texture: ResourcePathId,
        val uv: UV? = null,
        val rotation: Int = 0,
    ) {
        fun toMinecraft(): ModelElementFace {
            val modelElementTexture = if (uv == null) null else ModelElementTexture(uv.toFloatArray(), rotation)
            return ModelElementFace(cullFace?.toMinecraft(), tintIndex, texture.idWithHash, modelElementTexture)
        }
    }
}
