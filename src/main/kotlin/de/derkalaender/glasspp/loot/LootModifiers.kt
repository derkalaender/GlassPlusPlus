package de.derkalaender.glasspp.loot

import de.derkalaender.glasspp.util.rl
import net.minecraftforge.common.loot.GlobalLootModifierSerializer
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class LootModifiers {
    @SubscribeEvent
    fun registerModifierSerializers(event: RegistryEvent.Register<GlobalLootModifierSerializer<*>>) {
        event.registry.register(GlassShardModifier.Serializer.setRegistryName(rl("glass_shard")))
    }
}
