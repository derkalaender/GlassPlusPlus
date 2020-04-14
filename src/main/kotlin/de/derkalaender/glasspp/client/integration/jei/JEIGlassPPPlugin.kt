package de.derkalaender.glasspp.client.integration.jei

import de.derkalaender.glasspp.Registry
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.rl
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.ISubtypeRegistration

@JeiPlugin
class JEIGlassPPPlugin : IModPlugin {
    override fun getPluginUid() = rl("main")

    override fun registerItemSubtypes(registry: ISubtypeRegistration) {
        registry.registerSubtypeInterpreter(Registry.GLASS_SHARD.get()) {GlassShard.getSubtype(it)}
    }
}