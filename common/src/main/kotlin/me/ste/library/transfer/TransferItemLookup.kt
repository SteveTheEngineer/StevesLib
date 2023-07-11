package me.ste.library.transfer

import me.ste.library.lookup.item.ItemLookup
import me.ste.library.lookup.item.ItemLookupProvider
import me.ste.library.lookup.item.MutableItemLookup
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

class TransferItemLookup<I, O>(
    inCallback: ItemLookup<O>,

    private val outCallback: Consumer<Function<ItemStack, I?>>,
    private val outCallbackItem: BiConsumer<Item, Function<ItemStack, I?>>
) : ItemLookup<O> by inCallback {
    fun register(provider: Function<ItemStack, I?>) = this.outCallback.accept(provider)

    fun registerItem(item: Item, provider: Function<ItemStack, I?>) = this.outCallbackItem.accept(item, provider)
}