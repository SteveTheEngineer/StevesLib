package me.ste.library.menu

import dev.architectury.fluid.FluidStack
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.item.ItemStack
import java.util.function.Supplier

object MenuDataEntries {
    fun byte(valueSupplier: Supplier<Byte>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readByte) { buf, value -> buf.writeByte(value.toInt()) }
    fun short(valueSupplier: Supplier<Short>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readShort) { buf, value -> buf.writeShort(value.toInt()) }
    fun integer(valueSupplier: Supplier<Int>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readInt, FriendlyByteBuf::writeInt)
    fun long(valueSupplier: Supplier<Long>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readLong, FriendlyByteBuf::writeLong)
    fun float(valueSupplier: Supplier<Float>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readFloat, FriendlyByteBuf::writeFloat)
    fun double(valueSupplier: Supplier<Double>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readDouble, FriendlyByteBuf::writeDouble)
    fun boolean(valueSupplier: Supplier<Boolean>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean)
    fun string(valueSupplier: Supplier<String>) = SimpleMenuDataEntry(valueSupplier, FriendlyByteBuf::readUtf, FriendlyByteBuf::writeUtf)

    fun itemStack(valueSupplier: Supplier<ItemStack>) = SimpleMenuDataEntry({ valueSupplier.get().copy() }, FriendlyByteBuf::readItem, FriendlyByteBuf::writeItem)
    fun fluidStack(valueSupplier: Supplier<FluidStack>) = SimpleMenuDataEntry({ valueSupplier.get().copy() }, FluidStack::read) { buf, value -> value.write(buf) }

    fun itemResource(valueSupplier: Supplier<ItemResource>) = SimpleMenuDataEntry(valueSupplier, ItemResource::read) { buf, value -> value.write(buf) }
    fun fluidResource(valueSupplier: Supplier<FluidResource>) = SimpleMenuDataEntry(valueSupplier, FluidResource::read) { buf, value -> value.write(buf) }

    fun <T : Enum<T>> enumeration(valueSupplier: Supplier<T>, clazz: Class<T>) = SimpleMenuDataEntry(valueSupplier, { it.readEnum(clazz) }, FriendlyByteBuf::writeEnum)
}