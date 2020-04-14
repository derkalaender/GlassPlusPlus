package de.derkalaender.glasspp.recipe

import de.derkalaender.glasspp.Registry
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.toItemStack
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.crafting.SpecialRecipe
import net.minecraft.item.crafting.SpecialRecipeSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import java.util.function.Function

class GlassShardRecipe(
    id: ResourceLocation
) : SpecialRecipe(id) {
    companion object {
        val SERIALIZER = SpecialRecipeSerializer(Function(::GlassShardRecipe))
    }

    override fun matches(inv: CraftingInventory, worldIn: World) =
        inv.getAllSlotContents().filter { it.item == Registry.GLASS_SHARD.get() }.map { GlassShard.getGlassType(it) }
            .distinct().size == 1

    override fun getCraftingResult(inv: CraftingInventory) =
        inv.getAllSlotContents().first { it.item == Registry.GLASS_SHARD.get() }
            .let { GlassShard.getGlassType(it).getItem().toItemStack(1) }

    private fun CraftingInventory.getAllSlotContents() = (0 until sizeInventory).map { getStackInSlot(it) }

    override fun canFit(width: Int, height: Int) = width >= 2 && height >= 2

    override fun getSerializer() = SERIALIZER
}