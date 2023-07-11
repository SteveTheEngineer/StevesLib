package me.ste.library.simple.conversion

import me.ste.library.transfer.item.SnapshotItemContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack

interface StackSnapshotItemContainer<S> : SnapshotItemContainer<S> {
    fun getStack(side: Direction?, slot: Int): ItemStack
    fun getMaxStackSize(side: Direction?, slot: Int): Int

    fun accept(side: Direction?, slot: Int, stack: ItemStack): Int
    fun output(side: Direction?, slot: Int, amount: Int): Int

    override fun getResource(side: Direction?, slot: Int) = ResourceWithAmount.fromItemStack(
        this.getStack(side, slot)
    )

    override fun getCapacity(side: Direction?, slot: Int) = this.getMaxStackSize(side, slot).toLong()

    override fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.accept(side, slot, ResourceWithAmount.toItemStack(resource)).toLong()

    override fun output(side: Direction?, slot: Int, amount: Long) = this.output(side, slot, amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()).toLong()
}