package me.ste.library.lookup.item

import net.minecraft.world.item.Item

interface MutableItemLookup<T> {
    fun register(provider: ItemLookupProvider<T>)
    fun registerItem(item: Item, provider: ItemLookupProvider<T>)
}