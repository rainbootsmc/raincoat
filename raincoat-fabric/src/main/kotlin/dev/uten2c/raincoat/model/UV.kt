package dev.uten2c.raincoat.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = UV.Serializer::class)
data class UV(val x1: Float, val y1: Float, val x2: Float, val y2: Float) {
    fun toFloatArray(): FloatArray {
        val array = FloatArray(4)
        array[0] = x1
        array[1] = y1
        array[2] = x2
        array[3] = y2
        return array
    }

    @kotlinx.serialization.Serializer(forClass = UV::class)
    object Serializer : KSerializer<UV> {
        override fun deserialize(decoder: Decoder): UV {
            when (decoder) {
                is JsonDecoder -> {
                    val element = decoder.decodeJsonElement()
                    require(element is JsonArray)
                    return UV(
                        element[0].jsonPrimitive.float,
                        element[1].jsonPrimitive.float,
                        element[2].jsonPrimitive.float,
                        element[3].jsonPrimitive.float,
                    )
                }

                else -> {
                    return UV(
                        decoder.decodeFloat(),
                        decoder.decodeFloat(),
                        decoder.decodeFloat(),
                        decoder.decodeFloat(),
                    )
                }
            }
        }

        override fun serialize(encoder: Encoder, value: UV) {
            when (encoder) {
                is JsonEncoder -> encoder.encodeJsonElement(
                    JsonArray(
                        listOf(
                            JsonPrimitive(value.x1),
                            JsonPrimitive(value.y1),
                            JsonPrimitive(value.x2),
                            JsonPrimitive(value.y2),
                        ),
                    ),
                )

                else -> {
                    encoder.encodeFloat(value.x1)
                    encoder.encodeFloat(value.y1)
                    encoder.encodeFloat(value.x2)
                    encoder.encodeFloat(value.y2)
                }
            }
        }
    }
}
