package de.derkalaender.glasspp.util

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment

val MOD_BUS
    inline get() = FMLJavaModLoadingContext.get().modEventBus

inline fun <T> runForDist(clientTarget: () -> T, serverTarget: () -> T) = when (FMLEnvironment.dist) {
    Dist.CLIENT -> clientTarget()
    Dist.DEDICATED_SERVER -> serverTarget()
    else -> throw IllegalArgumentException("Unsided?")
}

fun Item.toItemStack(count: Int = 1) = ItemStack(this, count)
