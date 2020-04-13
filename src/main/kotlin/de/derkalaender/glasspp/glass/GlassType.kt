package de.derkalaender.glasspp.glass

import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.NBTDSLMarker
import de.derkalaender.glasspp.util.from
import de.derkalaender.glasspp.util.getAs
import de.derkalaender.glasspp.util.resourceLocation
import net.minecraft.block.Block
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.INBTSerializable

class GlassType() : INBTSerializable<CompoundNBT> {
    constructor(resourceLocation: ResourceLocation) : this() {
        this.resourceLocation = resourceLocation
    }

    @NBTDSLMarker
    private inner class GlassTypeNBT(nbt: CompoundNBT) {
        var resourceLocation by nbt.resourceLocation()
    }

    private var resourceLocation = "glass" from "minecraft"

    fun getResourceLocation() = resourceLocation

    fun getBlock() = resourceLocation.getAs<Block>()!!

    fun getItem() = getBlock().asItem()

    fun getGlassShard() = GlassShard.fromGlassType(this)

    override fun deserializeNBT(nbt: CompoundNBT) {
        resourceLocation = GlassTypeNBT(nbt).resourceLocation ?: GlassTypes.defaultGlassRegistryName
    }

    override fun serializeNBT() = CompoundNBT().also { GlassTypeNBT(it).resourceLocation = resourceLocation }

    override fun equals(other: Any?): Boolean {
        if (other is GlassType) {
            if (other.resourceLocation == resourceLocation) {
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int {
        return resourceLocation.hashCode()
    }

    override fun toString(): String {
        return "GlassType($resourceLocation)"
    }
}
