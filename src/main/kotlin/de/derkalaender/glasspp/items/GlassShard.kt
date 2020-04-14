package de.derkalaender.glasspp.items

import de.derkalaender.glasspp.GlassPP
import de.derkalaender.glasspp.Registry
import de.derkalaender.glasspp.glass.GlassType
import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.util.NBTDSLMarker
import de.derkalaender.glasspp.util.applyNBT
import de.derkalaender.glasspp.util.letNBT
import de.derkalaender.glasspp.util.serializable
import de.derkalaender.glasspp.util.toItemStack
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

class GlassShard : Item(
    Properties().group(GlassPP.CreativeTab)
) {
    @NBTDSLMarker
    class GlassShardNBT(nbt: CompoundNBT) {
        val glassType by nbt.serializable(::GlassType)
    }

    companion object {
        fun fromGlassType(glassType: GlassType) =
            Registry.GLASS_SHARD.get().toItemStack().applyNBT(::GlassShardNBT) { this.glassType.set(glassType) }

        fun getGlassType(stack: ItemStack) = stack.letNBT(::GlassShardNBT) { glassType.get() }
    }

    private fun isBroken(stack: ItemStack) = !GlassTypes.getAll().contains(getGlassType(stack))

    override fun fillItemGroup(group: ItemGroup, items: NonNullList<ItemStack>) {
        if (isInGroup(group)) {
            val glassTypes = mutableSetOf(GlassTypes.default)
            glassTypes.addAll(GlassTypes.getAll())

            items.addAll(glassTypes.map { it.getGlassShard() })
        }
    }

    override fun getDisplayName(stack: ItemStack): ITextComponent {
        return if (!isBroken(stack)) {
            val glassDisplayName = getGlassType(stack).getItem().toItemStack().displayName

            glassDisplayName.appendText(" ").appendSibling(TranslationTextComponent(getTranslationKey(stack)))
        } else {
            TranslationTextComponent(getTranslationKey(stack) + ".error")
        }
    }

    // Super duper bad way to enforce default nbt data
    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (isBroken(stack)) {
            stack.applyNBT(::GlassShardNBT) {
                glassType.set(GlassTypes.default)
            }
        }
    }
}
