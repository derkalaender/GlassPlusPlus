package de.derkalaender.glasspp.util.data

import de.derkalaender.glasspp.MODID
import java.util.Locale
import net.minecraft.data.DataGenerator
import net.minecraftforge.common.data.LanguageProvider

class Languages(gen: DataGenerator) {
    fun Locale.forMinecraft() = "${language}_$country"

    init {
        gen.addProvider(English(gen))
        gen.addProvider(German(gen))
    }

    inner class English(gen: DataGenerator) : LanguageProvider(gen, MODID, Locale.US.forMinecraft()) {
        override fun addTranslations() {
            add("itemGroup.glasspp", "Glass++")
            add("item.glasspp.glass_shard", "Shard")
            add("item.glasspp.glass_shard.error", "Ooops, this shouldn't have happened!")
        }
    }

    inner class German(gen: DataGenerator) : LanguageProvider(gen, MODID, Locale.GERMANY.forMinecraft()) {
        override fun addTranslations() {
            add("itemGroup.glasspp", "Glass++")
            add("item.glasspp.glass_shard", "Scherbe")
            add("item.glasspp.glass_shard.error", "Ups, das hätte aber nicht passieren dürfen!")
        }
    }
}
