package dev.uten2c.raincoat;

import dev.uten2c.raincoat.direction.DirectionListener;
import dev.uten2c.raincoat.keybinding.KeyBindings;
import dev.uten2c.raincoat.option.Options;
import dev.uten2c.raincoat.resource.ScaleMapReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class RaincoatMod implements ClientModInitializer {
    public static final String MOD_ID = "raincoat";

    @Override
    public void onInitializeClient() {
        Options.load();

        Networking.registerListeners();
        KeyBindings.register();
        DirectionListener.register();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ScaleMapReloadListener());
    }
}
