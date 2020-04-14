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
            val allStacks = mutableListOf<ItemStack>()
            for (x in 0 ..inv.width) {
                for (y in 0..inv.height) {
                    allStacks.add(inv.getStackInSlot(x + y * width))
                }
            }
            val actualGlassTypes = allStacks.filter { it.item is GlassShard }.map { GlassShard.getGlassType(it) }.distinct()
            if (actualGlassTypes.size == 1) {
                return actualGlassTypes.first().getItem().toItemStack(recipeOutput.count)
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