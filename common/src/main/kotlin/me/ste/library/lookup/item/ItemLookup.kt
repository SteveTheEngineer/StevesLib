package me.ste.library.lookup.item

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.function.Consumer

fun interface ItemLookup<T> {
    fun get(stack: ItemStack, setStack: Consumer<ItemStack>): T?
}