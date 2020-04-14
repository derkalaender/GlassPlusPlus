package de.derkalaender.glasspp.recipe

import com.google.gson.JsonObject
import de.derkalaender.glasspp.Registry
import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.toItemStack
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.Ingredient
import net.minecraft.item.crafting.ShapedRecipe
import net.minecraft.network.PacketBuffer
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistryEntry

class GlassShardRecipe(
    id: ResourceLocation,
    group: String,
    recipeWidth: Int,
    recipeHeight: Int,
    ingredients: NonNullList<Ingredient>,
    private val recipeOutput: ItemStack
) : ShapedRecipe(id, group, recipeWidth, recipeHeight, ingredients, recipeOutput) {
    override fun getSerializer() = Registry.GLASS_SHARD_RECIPE_SERIALIZER.get()

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun isDynamic() = true

    override fun getCraftingResult(inv: CraftingInventory): ItemStack {
        return if (recipeOutput.item.registryName in GlassTypes.getAll().map { it.getResourceLocation() }) {
            val matchingStacks =
                ingredients.flatMap { it.matchingStacks.toList() }.filter { it.item is GlassShard }.onEach { println(it.displayName.formattedText) }.distinct()
            println(matchingStacks.size)
            matchingStacks.forEach { println(it.displayName.formattedText) }

            if (matchingStacks.size == 1) {
                GlassShard.getGlassType(matchingStacks.first()).getItem().toItemStack(recipeOutput.count)
            } else {
                ItemStack.EMPTY
            }
        } else {
            recipeOutput.copy()
        }
    }

    class Serializer : ForgeRegistryEntry<IRecipeSerializer<*>>(), IRecipeSerializer<GlassShardRecipe> {

        private val delegatedSerializer = ShapedRecipe.Serializer()

        override fun read(recipeId: ResourceLocation, json: JsonObject): GlassShardRecipe {

            return delegatedSerializer.read(recipeId, json).toGlassShardRecipe()
        }

        override fun read(recipeId: ResourceLocation, buffer: PacketBuffer): GlassShardRecipe? {
            return delegatedSerializer.read(recipeId, buffer)?.toGlassShardRecipe()
        }

        override fun write(buffer: PacketBuffer, recipe: GlassShardRecipe) {
            delegatedSerializer.write(buffer, recipe.toShapedRecipe())
        }

        private fun ShapedRecipe.toGlassShardRecipe() =
            GlassShardRecipe(id, group, recipeWidth, recipeHeight, ingredients, recipeOutput)

        private fun GlassShardRecipe.toShapedRecipe() =
            ShapedRecipe(id, group, recipeWidth, recipeHeight, ingredients, recipeOutput)
    }
}