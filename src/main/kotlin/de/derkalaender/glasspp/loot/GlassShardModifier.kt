package de.derkalaender.glasspp.loot

import com.google.gson.JsonObject
import de.derkalaender.glasspp.glass.GlassType
import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.applyNBT
import de.derkalaender.glasspp.util.rl
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootParameter
import net.minecraft.world.storage.loot.LootParameterSet
import net.minecraft.world.storage.loot.LootParameterSets
import net.minecraft.world.storage.loot.LootParameters
import net.minecraft.world.storage.loot.LootTable
import net.minecraft.world.storage.loot.conditions.ILootCondition
import net.minecraftforge.common.loot.GlobalLootModifierSerializer
import net.minecraftforge.common.loot.LootModifier
import org.apache.commons.lang3.RandomStringUtils

class GlassShardModifier(conditions: Array<out ILootCondition>) : LootModifier(conditions) {
    // This is used as a flag to prevent endless loops (has random characters to prevent accidental use in jsons)
    // We can maybe substitute this with a simple boolean, but I don't know if forge calls #doApply in parallel
    private val stackOverflowParameter by lazy { LootParameter<Unit>(rl(RandomStringUtils.randomNumeric(5))) }
    private val stackOverflowParameterSet by lazy {
        LootParameterSet.Builder().apply {
            // Here we take the loot parameters from standard BLOCK set and add our own. We have to do it manually...
            val blockParameterSet = LootParameterSets.BLOCK
            blockParameterSet.requiredParameters.forEach { required(it) }
            (blockParameterSet.allParameters - blockParameterSet.requiredParameters).forEach { optional(it) }

            required(stackOverflowParameter)
        }.build()
    }

    override fun doApply(generatedLoot: MutableList<ItemStack>, context: LootContext): MutableList<ItemStack> {
        // Return early if our flag is set to prevent an infinite loop and therefore a stack overflow error
        if (context.has(stackOverflowParameter)) return generatedLoot

        val destroyedBlock = context.get(LootParameters.BLOCK_STATE)?.block ?: return generatedLoot
        val destroyedGlassType = GlassType(destroyedBlock.registryName!!)

        // Check if the destroyed block is a glass block
        // Can't do this with json conditions as we basically check for tag
        if (destroyedGlassType in GlassTypes.getAll()) {
            val newCtx = LootContext.Builder(context).withParameter(stackOverflowParameter, Unit)
                .build(stackOverflowParameterSet)

            // Get loot from json to enable custom rolls
            // Prefer specific loot table over global one
            val lootTable = context.world.server.lootTableManager.let { manager ->
                manager.getLootTableFromLocation(rl("glass_shard/${destroyedGlassType.getResourceLocation()}"))
                    .takeIf { it != LootTable.EMPTY_LOOT_TABLE }
                    ?: manager.getLootTableFromLocation(rl("glass_shard/global"))
            }

            // Only allow glass shards as added loot and map them to their correct type
            generatedLoot.addAll(lootTable.generate(newCtx).filter { it.item.registryName == rl("glass_shard") }
                .map { it.applyNBT(GlassShard::GlassShardNBT) { glassType.set(destroyedGlassType) } })
        }

        return generatedLoot
    }

    object Serializer : GlobalLootModifierSerializer<GlassShardModifier>() {
        override fun read(
            name: ResourceLocation,
            json: JsonObject,
            conditions: Array<out ILootCondition>?
        ): GlassShardModifier {
            return GlassShardModifier(conditions ?: emptyArray())
        }
    }
}
