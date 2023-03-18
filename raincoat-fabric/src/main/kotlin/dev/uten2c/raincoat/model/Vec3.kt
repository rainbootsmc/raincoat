package dev.uten2c.raincoat.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.joml.Vector3f

@Serializable(with = Vec3.Vec3Serializer::class)
data class Vec3(val x: Float, val y: Float, val z: Float) {
    constructor(xyz: Float) : this(xyz, xyz, xyz)

    fun add(vec3: Vec3): Vec3 {
        return Vec3(x + vec3.x, y + vec3.y, z + vec3.z)
    }

    operator fun minus(other: Vec3): Vec3 {
        return Vec3(x - other.x, y - other.y, z - other.z)
    }

    fun toMinecraft(): Vector3f {
        return Vector3f(x, y, z)
    }

    companion object {
        val zero = Vec3(0f)
        val one = Vec3(1f)
    }

    @Serializer(forClass = Vec3::class)
    object Vec3Serializer : KSerializer<Vec3> {
        override fun deserialize(decoder: Decoder): Vec3 {
            return if (decoder is JsonDecoder) {
                val element = decoder.decodeJsonElement()
                require(element is JsonArray)
                Vec3(element[0].jsonPrimitive.float, element[1].jsonPrimitive.float, element[2].jsonPrimitive.float)
            } else {
                Vec3(decoder.decodeFloat(), decoder.decodeFloat(), decoder.decodeFloat())
            }
        }

        override fun serialize(encoder: Encoder, value: Vec3) {
            if (encoder is JsonEncoder) {
                encoder.encodeJsonElement(
                    JsonArray(
                        listOf(
                            JsonPrimitive(value.x),
                            JsonPrimitive(value.y),
                            JsonPrimitive(value.z),
                        ),
                    ),
                )
            } else {
                encoder.encodeFloat(value.x)
                encoder.encodeFloat(value.y)
                encoder.encodeFloat(value.z)
            }
        }
    }
}
