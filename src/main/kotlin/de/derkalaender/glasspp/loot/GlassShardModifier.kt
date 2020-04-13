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
import net.minecraft.world.storage.loot.LootParameters
import net.minecraft.world.storage.loot.LootTable
import net.minecraft.world.storage.loot.conditions.ILootCondition
import net.minecraftforge.common.loot.GlobalLootModifierSerializer
import net.minecraftforge.common.loot.LootModifier

class GlassShardModifier(conditions: Array<out ILootCondition>) : LootModifier(conditions) {
    // This is used as a flag to prevent endless loops (has random characters to prevent accidental use in jsons)
    var generatingLoot = false

    override fun doApply(generatedLoot: MutableList<ItemStack>, context: LootContext): MutableList<ItemStack> {
        val destroyedBlock = context.get(LootParameters.BLOCK_STATE)?.block ?: return generatedLoot
        val destroyedGlassType = GlassType(destroyedBlock.registryName!!)

        // Check if the destroyed block is a glass block
        // Can't do this with json conditions as we basically check for the glass tag
        // Additionally, skip if our flag is set to prevent an infinite loop and therefore a stack overflow error
        if (destroyedGlassType in GlassTypes.getAll() && !generatingLoot) {
            // Get loot from json to enable custom rolls
            // Prefer specific loot table over global one
            val lootTable = context.world.server.lootTableManager.let { manager ->
                val rl = destroyedGlassType.getResourceLocation()
                manager.getLootTableFromLocation(rl("glass_shard/${rl.namespace}/${rl.path}"))
                    .takeIf { it != LootTable.EMPTY_LOOT_TABLE }
                    ?: manager.getLootTableFromLocation(rl("glass_shard/global"))
            }

            generatingLoot = true

            // Only allow glass shards to be added as loot and map them to their correct type
            generatedLoot.addAll(lootTable.generate(context).filter { it.item.registryName == rl("glass_shard") }
                .map { it.applyNBT(GlassShard::GlassShardNBT) { glassType.set(destroyedGlassType) } })

            generatingLoot = false
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
