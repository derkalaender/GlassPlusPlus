package de.derkalaender.glasspp

import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.recipe.GlassShardRecipe
import de.derkalaender.glasspp.util.MOD_BUS
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object Registry {
    val ITEMS = DeferredRegister(ForgeRegistries.ITEMS, MODID)

    val GLASS_SHARD = ITEMS.register("glass_shard", ::GlassShard)

    val RECIPE_SERIALIZERS = DeferredRegister(ForgeRegistries.RECIPE_SERIALIZERS, MODID)

    val GLASS_SHARD_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("glass_shard", GlassShardRecipe::Serializer)

    fun register() {
        ITEMS.register(MOD_BUS)
        RECIPE_SERIALIZERS.register(MOD_BUS)
    }
}
