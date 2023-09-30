package dev.uten2c.raincoat

import dev.uten2c.raincoat.direction.DirectionListener
import dev.uten2c.raincoat.keybinding.KeyBindings
import dev.uten2c.raincoat.model.RaincoatModelProvider
import dev.uten2c.raincoat.model.RaincoatModelReloadListener
import dev.uten2c.raincoat.network.Networking
import dev.uten2c.raincoat.network.PingListener
import dev.uten2c.raincoat.option.OptionManager
import dev.uten2c.raincoat.resource.FieldObjectReloadListener
import dev.uten2c.raincoat.resource.ScaleMapReloadListener
import dev.uten2c.raincoat.sign.SignListener
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier


class RaincoatMod : ClientModInitializer {
    override fun onInitializeClient() {
        OptionManager.load()

        Networking.registerListeners()
        KeyBindings.register()
        DirectionListener.register()
        PingListener.register()
        SignListener.register()

        Registry.register(Registries.ITEM_GROUP, Identifier(MOD_ID, "field_object_0_barrier"), fieldObjectItemGroupBarrier)
        Registry.register(Registries.ITEM_GROUP, Identifier(MOD_ID, "field_object_1_air"), fieldObjectItemGroupAir)

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(FieldObjectReloadListener)
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ScaleMapReloadListener())
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(RaincoatModelReloadListener())

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(::RaincoatModelProvider)
    }
}

