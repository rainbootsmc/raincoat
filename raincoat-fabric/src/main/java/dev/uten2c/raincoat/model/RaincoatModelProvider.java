package dev.uten2c.raincoat.model;

import com.mojang.logging.LogUtils;
import dev.uten2c.raincoat.RaincoatMod;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;

public class RaincoatModelProvider implements ModelResourceProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceManager manager;

    public RaincoatModelProvider(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
        if (resourceId.getNamespace().equals(RaincoatMod.MOD_ID)) {
            final var resource = manager.getResource(new Identifier(RaincoatMod.MOD_ID, "models/" + resourceId.getPath() + ".rainmodel"));
            if (resource.isPresent()) {
                final var modelResource = resource.get();
                try {
                    final var gunModel = GunModel.fromByteArray(modelResource.getInputStream().readAllBytes());
                    return new GunUnbakedModel(gunModel);
                } catch (IOException e) {
                    LOGGER.error("GunModel Parse Error", e);
                    return null;
                }
            }
        }
        return null;
    }
}
