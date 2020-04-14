package de.derkalaender.glasspp

import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.recipe.GlassShardRecipe
import de.derkalaender.glasspp.util.MOD_BUS
import de.derkalaender.glasspp.util.rl
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object Registry {
    val ITEMS = DeferredRegister(ForgeRegistries.ITEMS, MODID)

    val GLASS_SHARD = ITEMS.register("glass_shard", ::GlassShard)

    fun register() {
        MOD_BUS.register(this)

        ITEMS.register(MOD_BUS)
    }

    @SubscribeEvent
    fun registerRecipeSerializers(event: RegistryEvent.Register<IRecipeSerializer<*>>) {
        event.registry.register(GlassShardRecipe.SERIALIZER.setRegistryName(rl("glass_from_shards")))
    }
}
