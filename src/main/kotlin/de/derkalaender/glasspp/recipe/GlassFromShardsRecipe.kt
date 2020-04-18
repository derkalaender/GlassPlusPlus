package de.derkalaender.glasspp.recipe

import de.derkalaender.glasspp.Registry
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.toItemStack
import java.util.function.Function
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.crafting.SpecialRecipe
import net.minecraft.item.crafting.SpecialRecipeSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

class GlassFromShardsRecipe(
    id: ResourceLocation
) : SpecialRecipe(id) {
    companion object {
        val SERIALIZER = SpecialRecipeSerializer(Function(::GlassFromShardsRecipe))
    }

    // Only match if all 4 items are the same glass shards
    override fun matches(inv: CraftingInventory, worldIn: World) =
        inv.getAllSlotContents()
            .filter { !it.isEmpty }
            .let {
                it.size == 4 &&
                    it.all { stack -> stack.item == Registry.GLASS_SHARD.get() } &&
                    it.map { stack -> GlassShard.getGlassType(stack) }.distinct().size == 1
            }

    override fun getCraftingResult(inv: CraftingInventory) =
        inv.getAllSlotContents()
            .first { it.item == Registry.GLASS_SHARD.get() }
            .let { GlassShard.getGlassType(it).getItem().toItemStack(1) }

    private fun CraftingInventory.getAllSlotContents() = (0 until sizeInventory).map { getStackInSlot(it) }

    override fun canFit(width: Int, height: Int) = width >= 2 && height >= 2

    override fun getSerializer() = SERIALIZER
}
