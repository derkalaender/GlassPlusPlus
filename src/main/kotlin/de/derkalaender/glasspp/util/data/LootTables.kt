package de.derkalaender.glasspp.util.data

import com.mojang.datafixers.util.Pair
import de.derkalaender.glasspp.Registry
import de.derkalaender.glasspp.util.rl
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import net.minecraft.advancements.criterion.EnchantmentPredicate
import net.minecraft.advancements.criterion.ItemPredicate
import net.minecraft.advancements.criterion.MinMaxBounds
import net.minecraft.data.DataGenerator
import net.minecraft.data.LootTableProvider
import net.minecraft.enchantment.Enchantments
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.loot.ItemLootEntry
import net.minecraft.world.storage.loot.LootParameterSet
import net.minecraft.world.storage.loot.LootParameterSets
import net.minecraft.world.storage.loot.LootPool
import net.minecraft.world.storage.loot.LootTable
import net.minecraft.world.storage.loot.LootTableManager
import net.minecraft.world.storage.loot.RandomValueRange
import net.minecraft.world.storage.loot.ValidationTracker
import net.minecraft.world.storage.loot.conditions.Inverted
import net.minecraft.world.storage.loot.conditions.MatchTool

class LootTables(gen: DataGenerator) : LootTableProvider(gen) {
    override fun getTables(): MutableList<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> {
        return mutableListOf(
            Pair.of(
                Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>(::MyLootTables),
                LootParameterSets.BLOCK
            )
        )
    }

    override fun validate(map: MutableMap<ResourceLocation, LootTable>, validationtracker: ValidationTracker) {
        map.forEach { (name, table) -> LootTableManager.func_227508_a_(validationtracker, name, table) }
    }

    private inner class MyLootTables : Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        override fun accept(c: BiConsumer<ResourceLocation, LootTable.Builder>) {
            c.accept(
                rl("glass_shard/global"), LootTable.builder().addLootPool(
                    LootPool.builder()
                        .name("pool1")
                        .acceptCondition(
                            Inverted.builder(
                                MatchTool.builder(
                                    ItemPredicate.Builder.create()
                                        .enchantment(
                                            EnchantmentPredicate(
                                                Enchantments.SILK_TOUCH,
                                                MinMaxBounds.IntBound.atLeast(1)
                                            )
                                        )
                                )
                            )
                        )
                        .rolls(RandomValueRange(1f, 3f))
                        .addEntry(ItemLootEntry.builder(Registry.GLASS_SHARD::get))
                )
            )
        }
    }
}
