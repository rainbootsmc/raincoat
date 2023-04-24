package dev.uten2c.raincoat.model

import com.mojang.logging.LogUtils
import dev.uten2c.raincoat.MOD_ID
import dev.uten2c.raincoat.modelCbor
import kotlinx.serialization.decodeFromByteArray
import net.fabricmc.fabric.api.client.model.ModelProviderContext
import net.fabricmc.fabric.api.client.model.ModelResourceProvider
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.io.IOException

class RaincoatModelProvider(private val manager: ResourceManager) : ModelResourceProvider {
    override fun loadModelResource(resourceId: Identifier, context: ModelProviderContext): UnbakedModel? {
        if (resourceId.namespace == MOD_ID) {
            val resource = manager.getResource(Identifier(MOD_ID, "models/" + resourceId.path + ".rainmodel"))
            if (resource.isPresent) {
                val modelResource = resource.get()
                return try {
                    modelResource.inputStream.use {
                        val gunModel = modelCbor.decodeFromByteArray<GunModel>(it.readAllBytes())
                        GunUnbakedModel(gunModel)
                    }
                } catch (e: IOException) {
                    LOGGER.error("GunModel Parse Error", e)
                    null
                }
            }
        }
        return null
    }

    companion object {
        private val LOGGER = LogUtils.getLogger()
    }
}
