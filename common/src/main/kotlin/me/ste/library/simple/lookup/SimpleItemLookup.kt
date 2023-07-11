package me.ste.library.simple.lookup

import me.ste.library.lookup.item.ItemLookup
import me.ste.library.lookup.item.ItemLookupProvider
import me.ste.library.lookup.item.MutableItemLookup
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.BiFunction
import java.util.function.Consumer

class SimpleItemLookup<T> : ItemLookup<T>, MutableItemLookup<T> {
    private val providers = mutableListOf<ItemLookupProvider<T>>()
    private val items = mutableMapOf<Item, ItemLookupProvider<T>>()

    override fun get(stack: ItemStack, setStack: Consumer<ItemStack>) = this.providers.firstNotNullOfOrNull { it.get(stack, setStack) }

    override fun register(provider: ItemLookupProvider<T>) {
        this.providers += provider
    }

    override fun registerItem(item: Item, provider: ItemLookupProvider<T>) {
        this.items[item] = provider
    }

    init {
        this.register { stack, setStack ->
            this.items[stack.item]?.get(stack, setStack)
        }
    }
}