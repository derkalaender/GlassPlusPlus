package de.derkalaender.glasspp.client.model

import de.derkalaender.glasspp.util.rl
import de.derkalaender.glasspp.util.with
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModelHandler {
    @SubscribeEvent
    fun onModelBake(event: ModelBakeEvent) {
        val glassShardModelKey = rl("glass_shard") with "inventory"
        val originalGlassShardModel = event.modelRegistry[glassShardModelKey]
        if(originalGlassShardModel != null) {
            event.modelRegistry[glassShardModelKey] = DynamicGlassShardModel(event.modelLoader, originalGlassShardModel)
        }

    }
}