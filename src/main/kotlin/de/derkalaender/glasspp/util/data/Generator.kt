package de.derkalaender.glasspp.util.data

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent

class Generator {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        // val helper = event.existingFileHelper

        if(event.includeServer()) {
            generator.addProvider(LootTables(generator))
        }

        if (event.includeClient()) {
            Languages(generator)
        }
    }
}
