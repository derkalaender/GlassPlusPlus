package de.derkalaender.glasspp.util

import de.derkalaender.glasspp.MODID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryManager

fun rl(path: String) = path from MODID

infix fun String.from(namespace: String) = ResourceLocation(namespace, this)

infix fun ResourceLocation.with(variant: String) = ModelResourceLocation(this, variant)

inline fun <reified T : IForgeRegistryEntry<T>> getRegistryFor(): IForgeRegistry<T> =
    RegistryManager.ACTIVE.getRegistry(T::class.java)

inline fun <reified T : IForgeRegistryEntry<T>> ResourceLocation.getAs() = getRegistryFor<T>()
    .getValue(this)

inline fun <reified T : IForgeRegistryEntry<T>> registry(namespace: String) = object {
    operator fun provideDelegate(thisref: Any, property: KProperty<*>) =
        NullableRegistryDelegate<T>(getRegistryFor(), property.name from namespace)

    fun nonnull() = object {
        operator fun provideDelegate(thisref: Any, property: KProperty<*>) =
            NonnullRegistryDelegate<T>(getRegistryFor(), property.name from namespace)
    }
}

inline fun <reified T : IForgeRegistryEntry<T>> registry(resource: ResourceLocation) = object {
    operator fun provideDelegate(thisref: Any, property: KProperty<*>) =
        NullableRegistryDelegate<T>(getRegistryFor(), resource)

    fun nonnull() = object {
        operator fun provideDelegate(thisref: Any, property: KProperty<*>) =
            NonnullRegistryDelegate<T>(getRegistryFor(), resource)
    }
}

class NullableRegistryDelegate<T : IForgeRegistryEntry<T>>(
    private val registry: IForgeRegistry<T>,
    private val resource: ResourceLocation
) : ReadOnlyProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return registry.getValue(resource)
    }
}

class NonnullRegistryDelegate<T : IForgeRegistryEntry<T>>(
    private val registry: IForgeRegistry<T>,
    private val resource: ResourceLocation
) : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return registry.getValue(resource)
            ?: throw NoSuchElementException("Entry $resource is missing in the registry ${registry.registryName}.")
    }
}
