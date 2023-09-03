package dev.uten2c.raincoat.mixin.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(JsonUnbakedModel.class)
public interface JsonUnbakedModelAccessor {
    @Nullable
    @Accessor("parentId")
    Identifier getParentId();

    @Accessor("elements")
    List<ModelElement> getRawElements();

    @Nullable
    @Accessor("ambientOcclusion")
    Boolean getAmbientOcclusion();

    @Nullable
    @Accessor("textureMap")
    Map<String, Either<SpriteIdentifier, String>> getTextureMap();

    @Nullable
    @Accessor("guiLight")
    JsonUnbakedModel.GuiLight getRawGuiLight();

    @Accessor("transformations")
    ModelTransformation getRawTransformations();
}
