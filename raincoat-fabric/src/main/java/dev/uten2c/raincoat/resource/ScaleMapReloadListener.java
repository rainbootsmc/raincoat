package dev.uten2c.raincoat.resource;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScaleMapReloadListener implements SimpleSynchronousResourceReloadListener {
    private final Gson gson = new Gson();
    private static Map<@NotNull String, @NotNull Double> scaleMap = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("raincoat", "scale_map");
    }

    @Override
    public void reload(ResourceManager manager) {
        manager.getResource(new Identifier("raincoat", "scales.json")).ifPresent(resource -> {
            try (var reader = resource.getReader()) {
                //noinspection unchecked
                scaleMap = gson.fromJson(reader, Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<@NotNull String, @NotNull Double> getScaleMap() {
        return scaleMap;
    }
}
