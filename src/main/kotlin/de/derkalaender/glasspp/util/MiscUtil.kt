package de.derkalaender.glasspp.util

import java.awt.Color
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
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

fun getAllDirections() =
    arrayOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN)

fun TextureAtlasSprite.getAllPixels(): List<Color> {
    val pixels = mutableListOf<Color>()
    for (i in 0 until frameCount) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                pixels.add(Color(getPixelRGBA(i, x, y), true))
            }
        }
    }
    return pixels
}

fun List<Color>.average(): Color {
    val sumR = sumBy { it.red }
    val sumG = sumBy { it.green }
    val sumB = sumBy { it.blue }
    val sumA = sumBy { it.alpha }
    return Color(sumR / size, sumG / size, sumB / size, sumA / size)
}

fun Color.toBGRA() = Color(blue, green, red, alpha)
