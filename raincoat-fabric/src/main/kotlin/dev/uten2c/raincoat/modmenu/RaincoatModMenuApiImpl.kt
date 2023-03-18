package dev.uten2c.raincoat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.uten2c.raincoat.option.RaincoatOptionsScreen
import net.minecraft.client.MinecraftClient

class RaincoatModMenuApiImpl : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            RaincoatOptionsScreen(it, MinecraftClient.getInstance().options)
        }
    }
}
