package dev.uten2c.raincoat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.uten2c.raincoat.screen.RaincoatOptionsScreen;
import net.minecraft.client.MinecraftClient;

public class RaincoatModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new RaincoatOptionsScreen(parent, MinecraftClient.getInstance().options);
    }
}
