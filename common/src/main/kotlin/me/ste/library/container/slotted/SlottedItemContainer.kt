package me.ste.library.container.slotted

import net.minecraft.world.item.ItemStack

interface SlottedItemContainer {
    val size: Int
    val isEmpty: Boolean

    fun getStack(slot: Int): ItemStack
    fun setStack(slot: Int, stack: ItemStack)

    fun canPlace(slot: Int, stack: ItemStack): Boolean
    fun canTake(slot: Int, stack: ItemStack): Boolean

    fun getMaxStackSize(slot: Int): Int
}