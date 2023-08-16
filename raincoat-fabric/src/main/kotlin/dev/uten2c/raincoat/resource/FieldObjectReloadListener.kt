package dev.uten2c.raincoat.resource

import dev.uten2c.raincoat.MOD_ID
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrDefault

class FieldObjectReloadListener : SimpleSynchronousResourceReloadListener {
    override fun getFabricId(): Identifier {
        return Identifier(MOD_ID, "field_object")
    }

    override fun reload(manager: ResourceManager) {
        manager.getResource(Identifier(MODEL_ITEM_PATH)).ifPresent { resource ->
            resource.inputStream.use { input ->
                val string = input.readAllBytes().toString(Charsets.UTF_8)
                val model = JsonUnbakedModel.deserialize(string)
                idMap = model.overrides
                    .filter { it.modelId.path.startsWith(BASE_PATH) }
                    .associate {
                        val modelId = it.modelId.path.replace(BASE_PATH, "")
                        val customModelData = it.streamConditions()
                            .filter { c -> c.type.path == "custom_model_data" }
                            .map { c -> c.threshold.toInt() }
                            .findFirst()
                            .getOrDefault(0)
                        modelId to customModelData
                    }
            }
        }
    }

    companion object {
        private const val MODEL_ITEM_PATH = "models/item/red_dye.json"
        private const val BASE_PATH = "rainboots/block/"

        @JvmStatic
        var idMap: Map<String, Int> = HashMap()
            private set
    }
}
