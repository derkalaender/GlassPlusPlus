package de.derkalaender.glasspp.glass

import de.derkalaender.glasspp.util.from
import net.minecraft.tags.BlockTags

object GlassTypes {
    val glassTag = "glass" from "forge"

    val default = GlassType()

    val defaultGlassRegistryName = "glass" from "minecraft"

    fun getAll() = BlockTags.getCollection().getOrCreate(glassTag).allElements.map { GlassType(it.registryName!!) }
}
