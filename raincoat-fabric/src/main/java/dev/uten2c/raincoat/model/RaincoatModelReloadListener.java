package dev.uten2c.raincoat.model;

import dev.uten2c.raincoat.RaincoatMod;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public final class RaincoatModelReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(RaincoatMod.MOD_ID, "rainmodel");
    }

    @Override
    public void reload(ResourceManager manager) {
        GunUnbakedModel.clearCache();
    }
}
