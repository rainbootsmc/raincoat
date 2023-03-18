package dev.uten2c.raincoat.resource

import dev.uten2c.raincoat.MOD_ID
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

class ScaleMapReloadListener : SimpleSynchronousResourceReloadListener {
    override fun getFabricId(): Identifier {
        return Identifier(MOD_ID, "scale_map")
    }

    override fun reload(manager: ResourceManager) {
        manager.getResource(Identifier("raincoat", "scales")).ifPresent { resource ->
            resource.inputStream.use { input ->
                kotlin.runCatching {
                    scaleMap = ProtoBuf.decodeFromByteArray(input.readAllBytes())
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        var scaleMap: Map<Int, Float> = HashMap()
            private set
    }
}
