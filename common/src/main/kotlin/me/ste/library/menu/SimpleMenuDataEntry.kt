package me.ste.library.menu

import net.minecraft.network.FriendlyByteBuf
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier

class SimpleMenuDataEntry<T>(
    private val valueSupplier: Supplier<T>,

    private val readFunction: Function<FriendlyByteBuf, T>,
    private val writeFunction: BiConsumer<FriendlyByteBuf, T>
) : MenuDataEntry {
    var value = valueSupplier.get()
        private set

    private var firstSync = true

    override val needsSync get() = this.firstSync || this.valueSupplier.get() != this.value

    override fun markSynced() {
        this.firstSync = false
        this.value = this.valueSupplier.get()
    }

    override fun write(buf: FriendlyByteBuf) {
        this.writeFunction.accept(buf, this.valueSupplier.get())
    }

    override fun read(buf: FriendlyByteBuf) {
        val value = this.readFunction.apply(buf)
        this.value = value
    }
}