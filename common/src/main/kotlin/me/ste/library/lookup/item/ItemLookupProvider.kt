package me.ste.library.lookup.item

import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

fun interface ItemLookupProvider<T> {
    fun get(stack: ItemStack, setStack: Consumer<ItemStack>): T?
}