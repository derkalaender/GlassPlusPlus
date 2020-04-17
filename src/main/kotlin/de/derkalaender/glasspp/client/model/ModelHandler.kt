package de.derkalaender.glasspp.client.model

import de.derkalaender.glasspp.util.rl
import de.derkalaender.glasspp.util.with
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModelHandler {
    @SubscribeEvent
    fun onModelRegistry(event: ModelRegistryEvent) {
        ModelLoader.addSpecialModel(rl("glass_shard/frame") with "inventory")
    }

    @SubscribeEvent
    fun onModelBake(event: ModelBakeEvent) {
        val shardModelKey = rl("glass_shard") with "inventory"
        val frameModelKey = rl("glass_shard/frame") with "inventory"

        val frameModel = event.modelRegistry[frameModelKey]

        if(frameModel != null) {
            event.modelRegistry[shardModelKey] = DynamicGlassShardModel(event.modelLoader, frameModel)
        }
    }
}