package dev.uten2c.raincoat.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ResourcePathId.Serializer::class)
data class ResourcePathId(val id: String) {
    val idWithHash: String = "#$id"

    object Serializer : KSerializer<ResourcePathId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResourcePathIdSerializer", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): ResourcePathId {
            val decodeString = decoder.decodeString()
            val id = if (decodeString.startsWith("#")) decodeString.drop(1) else decodeString
            return ResourcePathId(id)
        }

        override fun serialize(encoder: Encoder, value: ResourcePathId) {
            encoder.encodeString(value.idWithHash)
        }
    }
}
