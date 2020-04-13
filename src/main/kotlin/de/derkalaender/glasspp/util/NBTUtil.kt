package de.derkalaender.glasspp.util

import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.nbt.ByteArrayNBT
import net.minecraft.nbt.ByteNBT
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.EndNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBTType
import net.minecraft.nbt.IntArrayNBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.LongArrayNBT
import net.minecraft.nbt.LongNBT
import net.minecraft.nbt.ShortNBT
import net.minecraft.nbt.StringNBT
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.INBTSerializable

@DslMarker
annotation class NBTDSLMarker

fun CompoundNBT.byte(defaultValue: Byte) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getByte, CompoundNBT::putByte)

fun CompoundNBT.short(defaultValue: Short) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getShort, CompoundNBT::putShort)

fun CompoundNBT.int(defaultValue: Int) = SimpleNBTDelegate(this, defaultValue, CompoundNBT::getInt, CompoundNBT::putInt)
fun CompoundNBT.long(defaultValue: Long) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getLong, CompoundNBT::putLong)

fun CompoundNBT.float(defaultValue: Float) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getFloat, CompoundNBT::putFloat)

fun CompoundNBT.double(defaultValue: Double) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getDouble, CompoundNBT::putDouble)

fun CompoundNBT.string(defaultValue: String) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getString, CompoundNBT::putString)

fun CompoundNBT.byteArray(defaultValue: ByteArray = ByteArray(0)) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getByteArray, CompoundNBT::putByteArray)

fun CompoundNBT.intArray(defaultValue: IntArray = IntArray(0)) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getIntArray, CompoundNBT::putIntArray)

fun CompoundNBT.longArray(defaultValue: LongArray = LongArray(0)) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getLongArray, CompoundNBT::putLongArray)

fun CompoundNBT.boolean(defaultValue: Boolean) =
    SimpleNBTDelegate(this, defaultValue, CompoundNBT::getBoolean, CompoundNBT::putBoolean)

fun CompoundNBT.list(type: INBTType<*>, defaultValue: ListNBT = ListNBT()) = SimpleNBTDelegate(this, defaultValue, {
    getList(it, getTagIDForType(type))
}, { tag, value -> put(tag, value) })

fun CompoundNBT.uuid() = object : MutableNBTDelegate<UUID?>(this) {
    override fun getValue(nbt: CompoundNBT, tag: String): UUID? {
        return if (nbt.contains(tag + "Most") && nbt.contains(tag + "Least")) {
            nbt.getUniqueId(tag)
        } else {
            null
        }
    }

    override fun setValue(nbt: CompoundNBT, tag: String, value: UUID?) {
        if (value != null) {
            nbt.putUniqueId(tag, value)
        }
    }
}

fun CompoundNBT.resourceLocation() = object : MutableNBTDelegate<ResourceLocation?>(this) {
    override fun getValue(nbt: CompoundNBT, tag: String): ResourceLocation? {
        return if (nbt.contains(tag)) {
            ResourceLocation(nbt.getString(tag))
        } else {
            null
        }
    }

    override fun setValue(nbt: CompoundNBT, tag: String, value: ResourceLocation?) {
        if (value != null) {
            nbt.putString(tag, value.toString())
        }
    }
}

fun CompoundNBT.block() = object : MutableNBTDelegate<Block?>(this) {
    override fun getValue(nbt: CompoundNBT, tag: String): Block? {
        return if (nbt.contains(tag)) {
            ResourceLocation(nbt.getString(tag)).getAs<Block>()
        } else {
            null
        }
    }

    override fun setValue(nbt: CompoundNBT, tag: String, value: Block?) {
        if (value != null) {
            val registryName = value.registryName
            if (registryName != null) {
                nbt.putString(tag, registryName.toString())
            }
        }
    }
}

fun <T : Any> CompoundNBT.compound(nested: (CompoundNBT) -> T) = object : NBTDelegate<CompoundHelper<T>>(this) {
    override fun getValue(nbt: CompoundNBT, tag: String): CompoundHelper<T> {
        val compound = if (nbt.contains(tag)) nbt.getCompound(tag) else CompoundNBT()
        return CompoundHelper(nested(compound)) { nbt.put(tag, compound) }
    }
}

fun <T : INBTSerializable<CompoundNBT>> CompoundNBT.serializable(nbtSerializable: () -> T) =
    object : NBTDelegate<SerializableHelper<T>>(this) {
        override fun getValue(nbt: CompoundNBT, tag: String): SerializableHelper<T> {
            val compound = if (nbt.contains(tag)) nbt.getCompound(tag) else CompoundNBT()
            val serializable = nbtSerializable().apply { deserializeNBT(compound) }
            return SerializableHelper(serializable) { nbt.put(tag, it.serializeNBT()) }
        }
    }

class CompoundHelper<T : Any>(private val holder: T, private val callback: () -> Unit) {
    operator fun <R> invoke(nbt: T.() -> R): R {
        val result = nbt(holder)
        callback()
        return result
    }
}

class SerializableHelper<T : INBTSerializable<*>>(private val serializable: T, private val callback: (T) -> Unit) {
    operator fun <R> invoke(nbt: T.() -> R): R {
        val result = nbt(serializable)
        callback(serializable)
        return result
    }

    fun get() = serializable

    fun set(serializable: T) {
        callback(serializable)
    }
}

abstract class NBTDelegate<R>(private val nbt: CompoundNBT) : ReadOnlyProperty<Any, R> {

    abstract fun getValue(nbt: CompoundNBT, tag: String): R

    override fun getValue(thisRef: Any, property: KProperty<*>) = getValue(nbt, property.name)
}

abstract class MutableNBTDelegate<R>(private val nbt: CompoundNBT) : NBTDelegate<R>(nbt), ReadWriteProperty<Any, R> {
    abstract fun setValue(nbt: CompoundNBT, tag: String, value: R)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: R) = setValue(nbt, property.name, value)
}

class SimpleNBTDelegate<R : Any>(
    nbt: CompoundNBT,
    private val defaultValue: R,
    private val getter: CompoundNBT.(String) -> R,
    private val setter: CompoundNBT.(String, R) -> Unit
) : MutableNBTDelegate<R>(nbt) {
    override fun getValue(nbt: CompoundNBT, tag: String): R {
        return if (nbt.contains(tag)) {
            getter(nbt, tag)
        } else {
            defaultValue
        }
    }

    override fun setValue(nbt: CompoundNBT, tag: String, value: R) {
        setter(nbt, tag, value)
    }
}

val TAG_TYPES = arrayOf(
    EndNBT.TYPE,
    ByteNBT.TYPE,
    ShortNBT.TYPE,
    IntNBT.TYPE,
    LongNBT.TYPE,
    FloatNBT.TYPE,
    DoubleNBT.TYPE,
    ByteArrayNBT.TYPE,
    StringNBT.TYPE,
    ListNBT.TYPE,
    CompoundNBT.TYPE,
    IntArrayNBT.TYPE,
    LongArrayNBT.TYPE
)

fun getTagIDForType(type: INBTType<*>): Int = TAG_TYPES.indexOf(type)

fun <T : Any, R> ItemStack.letNBT(nbtClass: (CompoundNBT) -> T, nbt: T.() -> R): R {
    return nbt(nbtClass(orCreateTag))
}

fun <T : Any> ItemStack.applyNBT(nbtClass: (CompoundNBT) -> T, nbt: T.() -> Unit): ItemStack {
    nbtClass(orCreateTag).apply(nbt)
    return this
}
