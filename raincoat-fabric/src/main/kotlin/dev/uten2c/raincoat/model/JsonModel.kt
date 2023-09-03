package dev.uten2c.raincoat.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class JsonModel(
    val parent: ResourcePath? = null,
    val elements: List<ModelElement>? = null,
    val display: ModelTransformation? = null,
    val groups: List<GroupItem>? = null,
) {
    @Serializable(with = GroupItemSerializer::class)
    sealed class GroupItem {
        @Serializable
        data class Number(val id: Int) : GroupItem()

        @Serializable
        data class Group(
            val name: String? = null,
            val origin: Vec3,
            val color: Int? = null,
            val children: List<GroupItem>,
            val shade: Boolean? = null,

            // Blockbench
            val nbt: String? = null,
            val armAnimationEnabled: Boolean? = null,
        ) : GroupItem()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializer(forClass = GroupItem::class)
    object GroupItemSerializer : KSerializer<GroupItem> {
        override fun deserialize(decoder: Decoder): GroupItem {
            require(decoder is JsonDecoder)
            val element = decoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                return GroupItem.Number(element.jsonPrimitive.int)
            }
            return decoder.json.decodeFromJsonElement(GroupItem.Group.serializer(), element)
        }

        override fun serialize(encoder: Encoder, value: GroupItem) {
            require(encoder is JsonEncoder)
            val element = when (value) {
                is GroupItem.Group -> Json.encodeToJsonElement(value)
                is GroupItem.Number -> Json.encodeToJsonElement(value.id)
            }
            encoder.encodeJsonElement(element)
        }
    }
}
