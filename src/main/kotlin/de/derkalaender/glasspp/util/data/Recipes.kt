package de.derkalaender.glasspp.util.data

import de.derkalaender.glasspp.recipe.GlassShardRecipe
import de.derkalaender.glasspp.util.rl
import net.minecraft.data.CustomRecipeBuilder
import net.minecraft.data.DataGenerator
import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.RecipeProvider
import net.minecraft.item.crafting.SpecialRecipeSerializer
import java.util.function.Consumer

class Recipes(gen: DataGenerator) : RecipeProvider(gen) {
    override fun registerRecipes(consumer: Consumer<IFinishedRecipe>) {
        specialRecipe(consumer, GlassShardRecipe.SERIALIZER)
    }

    private fun specialRecipe(consumer: Consumer<IFinishedRecipe>, serializer: SpecialRecipeSerializer<*>) {
        CustomRecipeBuilder.customRecipe(serializer)
            .build(consumer, rl("dynamic/${serializer.registryName!!.path}").toString())
    }
}