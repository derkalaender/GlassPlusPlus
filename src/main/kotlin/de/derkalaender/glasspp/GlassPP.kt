package de.derkalaender.glasspp

import de.derkalaender.glasspp.client.proxy.ClientProxy
import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.loot.LootModifiers
import de.derkalaender.glasspp.proxy.IProxy
import de.derkalaender.glasspp.proxy.ServerProxy
import de.derkalaender.glasspp.util.MOD_BUS
import de.derkalaender.glasspp.util.data.Generator
import de.derkalaender.glasspp.util.runForDist
import net.minecraft.item.ItemGroup
import net.minecraftforge.fml.common.Mod

@Mod(MODID)
class GlassPP {
    var proxy: IProxy = runForDist({
        ClientProxy
    }, {
        ServerProxy
    })

    init {
        MOD_BUS.register(Generator())
        MOD_BUS.register(LootModifiers())
        proxy.registerHandlers()
        Registry.register()
    }

    object CreativeTab : ItemGroup(MODID) {
        override fun createIcon() = GlassTypes.default.getGlassShard()
    }
}
