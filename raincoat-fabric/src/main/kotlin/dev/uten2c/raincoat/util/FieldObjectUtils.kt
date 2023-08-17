package dev.uten2c.raincoat.util

import com.mojang.datafixers.util.Either
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.render.model.json.ModelOverride
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import java.util.concurrent.atomic.AtomicInteger

object FieldObjectUtils {
    private const val CUSTOM_MODEL_DATA_START = 1001

    @JvmStatic
    fun createFieldObjectModel(original: JsonUnbakedModel): JsonUnbakedModel {
        val id = AtomicInteger(CUSTOM_MODEL_DATA_START)
        val overrides = original.overrides.map { override ->
            val types = override.streamConditions().map { it.type }.toList()
            if (Identifier("preview") !in types) {
                return@map override
            }
            val conditions = listOf(ModelOverride.Condition(Identifier("custom_model_data"), id.getAndIncrement().toFloat()))
            ModelOverride(override.modelId, conditions)
        }
        return JsonUnbakedModel(
            Identifier("item/generated"),
            emptyList(),
            mapOf("layer0" to Either.left(SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier("item/red_dye")))),
            null,
            null,
            ModelTransformation.NONE,
            overrides,
        )
    }
}
