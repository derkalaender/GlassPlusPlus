package de.derkalaender.glasspp.client.model

import de.derkalaender.glasspp.util.rl
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModelHandler {
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        ModelLoaderRegistry.registerLoader(rl("glass_shard"), DynamicGlassShardModel.Loader)
    }
}