package de.derkalaender.glasspp

import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.MOD_BUS
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object Registry {
    val ITEMS = DeferredRegister(ForgeRegistries.ITEMS, MODID)

    val GLASS_SHARD = ITEMS.register("glass_shard", ::GlassShard)

    fun register() {
        ITEMS.register(MOD_BUS)
    }
}
