package de.derkalaender.glasspp.client.integration.jei

import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.recipe.GlassFromShardsRecipe
import de.derkalaender.glasspp.util.toItemStack
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.util.ResourceLocation

class GlassFromShardsRecipeWrapper(private val recipe: GlassFromShardsRecipe) : ICraftingCategoryExtension {
    override fun setIngredients(ingredients: IIngredients) {
        val allGlassTypes = GlassTypes.getAll()
        val inputs = allGlassTypes.map { it.getGlassShard() }.map { inputList -> List(4) { inputList } }
        val outputs = allGlassTypes.map { it.getItem().toItemStack() }.map { listOf(it) }

        ingredients.setInputLists(VanillaTypes.ITEM, inputs)
        ingredients.setOutputLists(VanillaTypes.ITEM, outputs)
    }

    override fun getRegistryName(): ResourceLocation {
        return recipe.id
    }
}
