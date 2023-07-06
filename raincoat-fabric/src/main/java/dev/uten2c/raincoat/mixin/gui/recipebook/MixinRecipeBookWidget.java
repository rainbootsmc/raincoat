package dev.uten2c.raincoat.mixin.gui.recipebook;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.recipebook.RecipeManager;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public class MixinRecipeBookWidget {
    @ModifyArg(method = "refreshResults", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;setResults(Ljava/util/List;Z)V"), index = 0)
    private List<RecipeResultCollection> sortResults(List<RecipeResultCollection> recipes) {
        if (!States.isOnServer()) {
            return recipes;
        }
        return RecipeManager.sorted(recipes);
    }
}
