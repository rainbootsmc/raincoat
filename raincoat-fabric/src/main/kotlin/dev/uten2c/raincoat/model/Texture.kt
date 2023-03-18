package dev.uten2c.raincoat.model

import com.mojang.datafixers.util.Either
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.screen.PlayerScreenHandler

@Serializable(with = Texture.Serializer::class)
sealed interface Texture {
    fun toMinecraft(): Either<SpriteIdentifier, String>

    @Serializable
    @SerialName("id")
    data class Id(val resourcePath: ResourcePath) : Texture {
        override fun toMinecraft(): Either<SpriteIdentifier, String> {
            return Either.left(SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, resourcePath.toMinecraft()))
        }
    }

    @Serializable
    @SerialName("ref")
    data class Ref(val target: String) : Texture {
        override fun toMinecraft(): Either<SpriteIdentifier, String> {
            return Either.right(target)
        }
    }

    object Serializer : KSerializer<Texture> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TextureSerializer", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Texture {
            val string = decoder.decodeString()
            return if (string.startsWith("#")) {
                Ref(string.substring(1))
            } else {
                Id(ResourcePath(string))
            }
        }

        override fun serialize(encoder: Encoder, value: Texture) {
            when (value) {
                is Ref -> encoder.encodeString("#${value.target}")
                is Id -> encoder.encodeString(value.resourcePath.toString())
            }
        }
    }
}
