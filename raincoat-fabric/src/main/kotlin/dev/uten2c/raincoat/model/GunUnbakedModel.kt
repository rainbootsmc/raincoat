package dev.uten2c.raincoat.model

import com.google.common.cache.CacheBuilder
import com.google.common.collect.Lists
import dev.uten2c.raincoat.util.StackUtils
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.render.model.json.ModelElement
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import java.util.function.Function

class GunUnbakedModel(private val gunModel: GunModel) : UnbakedModel {
    override fun getModelDependencies(): MutableCollection<Identifier> {
        return mutableListOf()
    }

    override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {
    }

    override fun bake(baker: Baker, textureGetter: Function<SpriteIdentifier, Sprite>, rotationContainer: ModelBakeSettings, modelId: Identifier): BakedModel {
        return GunBakedModel(textureGetter.apply(missingSpriteId), gunModel, textureGetter, rotationContainer, modelId)
    }

    companion object {
        private val missingSpriteId = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId())
        private val modelCache = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .build<Int, BakedModel>()
        private val dummyBaker = object : Baker {
            override fun getOrLoadModel(id: Identifier): UnbakedModel {
                throw AssertionError()
            }

            override fun bake(id: Identifier, settings: ModelBakeSettings): BakedModel? {
                throw AssertionError()
            }
        }

        @JvmStatic
        fun getOrCreateModel(stack: ItemStack, model: GunBakedModel): BakedModel? {
            if (stack.isEmpty) {
                return null
            }
            val gunModelState = StackUtils.getGunModelState(stack)
            if (gunModelState == -1) {
                return null
            }
            val cache = modelCache.getIfPresent(gunModelState)
            if (cache != null) {
                return cache
            }
            val bakedModel = createBakedModel(model.gunModel, gunModelState, model.textureGetter, model.rotationContainer, model.modelId) ?: return null
            modelCache.put(gunModelState, bakedModel)
            return bakedModel
        }

        private fun createBakedModel(gunModel: GunModel, gunModelState: Int, textureGetter: Function<SpriteIdentifier, Sprite>, rotationContainer: ModelBakeSettings, modelId: Identifier): BakedModel? {
            val removeIndices = gunModel.removeIndices[gunModelState] ?: emptyList()
            val modelTransformation = gunModel.modelTransformations[gunModelState]?.toMinecraft() ?: ModelTransformation.NONE
            val trimmedElements = Lists.newArrayList<ModelElement>()
            for (i in gunModel.elements.indices) {
                if (removeIndices.contains(i)) {
                    continue
                }
                trimmedElements.add(gunModel.elements[i].toMinecraft())
            }
            val textures = gunModel.textures
                .mapValues { it.value.toMinecraft() }
            val jsonUnbakedModel = JsonUnbakedModel(null, trimmedElements, textures, true, JsonUnbakedModel.GuiLight.ITEM, modelTransformation, emptyList())
            return jsonUnbakedModel.bake(dummyBaker, textureGetter, rotationContainer, modelId)
        }

        fun clearCache() {
            modelCache.cleanUp()
        }
    }
}
