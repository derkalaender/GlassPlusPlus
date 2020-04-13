package de.derkalaender.glasspp.client.proxy

import de.derkalaender.glasspp.client.model.ModelDynamicGlassShard
import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.proxy.IProxy
import de.derkalaender.glasspp.util.MOD_BUS
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object ClientProxy : IProxy {
    override fun registerHandlers() {
        println("REGISTER HANDLERS")
        MOD_BUS.register(this)
    }

    @SubscribeEvent
    fun onModelBake(event: ModelBakeEvent) {
        val original = event.modelManager.getModel(ModelDynamicGlassShard.LOCATION)
        event.modelRegistry[ModelDynamicGlassShard.LOCATION] =
            ModelDynamicGlassShard(true, original, GlassTypes.default)
    }
}
