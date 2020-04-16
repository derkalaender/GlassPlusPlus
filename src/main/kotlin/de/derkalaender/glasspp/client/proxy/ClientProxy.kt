package de.derkalaender.glasspp.client.proxy

import de.derkalaender.glasspp.client.model.ModelHandler
import de.derkalaender.glasspp.proxy.IProxy
import de.derkalaender.glasspp.util.MOD_BUS

object ClientProxy : IProxy {
    override fun registerHandlers() {
        MOD_BUS.register(ModelHandler)
    }
}
