package dev.uten2c.raincoat.mixin.render.model.model;

import dev.uten2c.raincoat.util.FieldObjectUtils;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

@Mixin(BakedModelManager.class)
public class MixinBakedModelManager {
    @Unique
    private Profiler raincoat$prepareProfiler;
    @Unique
    private Executor raincoat$prepareExecutor;

    @Shadow
    @Final
    private BlockColors colorMap;

    public MixinBakedModelManager() {
    }

    @Inject(method = "reload", at = @At("HEAD"))
    private void getArgs(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        raincoat$prepareProfiler = prepareProfiler;
        raincoat$prepareExecutor = prepareExecutor;
    }

    @Redirect(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCombineAsync(Ljava/util/concurrent/CompletionStage;Ljava/util/function/BiFunction;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<ModelLoader> loadPreviewParent(CompletableFuture<Map<Identifier, JsonUnbakedModel>> instance, CompletionStage<? extends Map<Identifier, List<ModelLoader.SourceTrackedData>>> other, BiFunction<? super Map<Identifier, JsonUnbakedModel>, ? super Map<Identifier, List<ModelLoader.SourceTrackedData>>, ? extends ModelLoader> fn, Executor executor) {
        return instance.thenCombineAsync(other, (jsonUnbakedModels, blockStates) -> {
            final var map = new HashMap<>(jsonUnbakedModels);
            final var redDyeId = new Identifier("models/item/red_dye.json");
            final var redDyeModel = map.get(redDyeId);
            if (redDyeModel != null) {
                map.put(redDyeId, FieldObjectUtils.createFieldObjectModel(redDyeModel));
            }
            return new ModelLoader(this.colorMap, raincoat$prepareProfiler, map, blockStates);
        }, raincoat$prepareExecutor);
    }
}
