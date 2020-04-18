package de.derkalaender.glasspp.client.model

import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

/*
A simple way to cache model overrides dynamically
 */
abstract class CachedDynamicModel<R : Any, T : IBakedModel>(private val cache: MutableMap<R, T>) : IBakedModel {
    abstract fun getKeyForStack(stack: ItemStack): R

    abstract fun getModelForStack(stack: ItemStack): T

    override fun getOverrides() = object : ItemOverrideList() {
        override fun getModelWithOverrides(
            model: IBakedModel,
            stack: ItemStack,
            worldIn: World?,
            entityIn: LivingEntity?
        ): IBakedModel {
            return cache.getOrPut(getKeyForStack(stack)) { getModelForStack(stack) }
        }
    }
}
