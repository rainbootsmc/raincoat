package dev.uten2c.raincoat.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public record GunUnbakedModel(@NotNull GunModel gunModel) implements UnbakedModel {
    private static final SpriteIdentifier MISSING_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId());
    private static final Cache<Integer, BakedModel> MODEL_CACHE = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .build();
    private static final Baker DUMMY_BAKER = new Baker() {
        @Override
        public UnbakedModel getOrLoadModel(Identifier id) {
            throw new AssertionError();
        }

        @Nullable
        @Override
        public BakedModel bake(Identifier id, ModelBakeSettings settings) {
            throw new AssertionError();
        }
    };

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
    }

    @Override
    public @NotNull BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return new GunBakedModel(textureGetter.apply(MISSING_SPRITE_ID), gunModel, textureGetter, rotationContainer, modelId);
    }

    public static @Nullable BakedModel getOrCreateModel(@NotNull ItemStack stack, @NotNull GunBakedModel model) {
        if (stack.isEmpty()) {
            return null;
        }
        final var gunModelState = StackUtils.getGunModelState(stack);
        if (gunModelState == -1) {
            return null;
        }
        final var cache = MODEL_CACHE.getIfPresent(gunModelState);
        if (cache != null) {
            return cache;
        }
        final var newModel = createBakedModel(model.gunModel(), gunModelState, model.textureGetter(), model.rotationContainer(), model.modelId());
        if (newModel == null) {
            return null;
        }
        MODEL_CACHE.put(gunModelState, newModel);
        return newModel;
    }

    private static @Nullable BakedModel createBakedModel(@NotNull GunModel gunModel, int gunModelState, @NotNull Function<SpriteIdentifier, Sprite> textureGetter, @NotNull ModelBakeSettings rotationContainer, @NotNull Identifier modelId) {
        final var removeIndices = gunModel.removeIndices().get(gunModelState);
        if (removeIndices == null) {
            return null;
        }
        final var modelTransformation = gunModel.modelTransformations().get(gunModelState);
        if (modelTransformation == null) {
            return null;
        }
        final var trimmedElements = Lists.<ModelElement>newArrayList();
        for (int i = 0; i < gunModel.elements().size(); i++) {
            if (removeIndices.contains(i)) {
                continue;
            }
            trimmedElements.add(gunModel.elements().get(i));
        }
        final var jsonUnbakedModel = new JsonUnbakedModel(null, trimmedElements, gunModel.textures(), true, JsonUnbakedModel.GuiLight.ITEM, modelTransformation, Collections.emptyList());
        return jsonUnbakedModel.bake(DUMMY_BAKER, textureGetter, rotationContainer, modelId);
    }

    public static void clearCache() {
        MODEL_CACHE.cleanUp();
    }
}
