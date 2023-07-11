package me.ste.library.lookup.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

interface MutableItemLookup<T> {
    fun register(provider: ItemLookupProvider<T>)
    fun registerItem(item: Item, provider: ItemLookupProvider<T>)
}