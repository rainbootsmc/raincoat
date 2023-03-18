package dev.uten2c.raincoat.model

import dev.uten2c.raincoat.MOD_ID
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

class RaincoatModelReloadListener : SimpleSynchronousResourceReloadListener {
    override fun getFabricId(): Identifier {
        return Identifier(MOD_ID, "rainmodel")
    }

    override fun reload(manager: ResourceManager) {
        GunUnbakedModel.clearCache()
    }
}
