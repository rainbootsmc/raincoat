package dev.uten2c.raincoat.recipebook

import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.recipe.Recipe

object RecipeManager {
    private var declaredRecipes: List<Recipe<*>> = emptyList()

    @JvmStatic
    fun declareRecipes(recipes: List<Recipe<*>>) {
        declaredRecipes = recipes
    }

    @JvmStatic
    fun sorted(recipes: List<RecipeResultCollection>): List<RecipeResultCollection> {
        return recipes.sortedBy { collection -> declaredRecipes.indexOf(collection.allRecipes.first()) }
    }
}
