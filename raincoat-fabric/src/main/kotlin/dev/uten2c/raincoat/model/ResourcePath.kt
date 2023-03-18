package dev.uten2c.raincoat.model

import dev.uten2c.raincoat.MINECRAFT
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.util.Identifier

@Serializable(with = ResourcePath.Serializer::class)
data class ResourcePath(val namespace: String, val path: String) {
    constructor(value: String) : this(
        if (":" in value) value.split(":")[0] else MINECRAFT,
        if (":" in value) value.split(":")[1] else value,
    )

    override fun toString(): String {
        if (namespace == MINECRAFT) {
            return path
        }
        return "$namespace:$path"
    }

    fun toMinecraft(): Identifier {
        return Identifier(namespace, path)
    }

    object Serializer : KSerializer<ResourcePath> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResourcePathSerializer", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): ResourcePath {
            val string = decoder.decodeString()
            return if (":" in string) {
                string.split(":").let { ResourcePath(it[0], it[1]) }
            } else {
                ResourcePath(MINECRAFT, string)
            }
        }

        override fun serialize(encoder: Encoder, value: ResourcePath) {
            val prefix = if (value.namespace == MINECRAFT) "" else "${value.namespace}:"
            encoder.encodeString(prefix + value.path)
        }
    }
}
