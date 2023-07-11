package me.ste.library.simple.wrapper

import me.ste.library.transfer.item.SimulatableItemContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack

class StackItemContainerWrapper(
    private val container: SimulatableItemContainer
) : SimulatableItemContainer by container {
    fun getStack(side: Direction?, slot: Int): ItemStack {
        val resource = this.getResource(side, slot)
        if (resource.resource.isEmpty) {
            return ItemStack.EMPTY
        }

        return ResourceWithAmount.toItemStack(resource.capToInt())
    }

    fun getMaxStackSize(side: Direction?, slot: Int) = this.getCapacity(side, slot).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    fun accept(side: Direction?, slot: Int, stack: ItemStack) =
        this.accept(side, slot, ResourceWithAmount.fromItemStack(stack)).toInt()

    fun output(side: Direction?, slot: Int, amount: Int) =
        this.output(side, slot, amount.toLong()).toInt()

    fun simulateAccept(side: Direction?, slot: Int, stack: ItemStack) =
        this.simulateAccept(side, slot, ResourceWithAmount.fromItemStack(stack)).toInt()

    fun simulateOutput(side: Direction?, slot: Int, amount: Int) =
        this.simulateOutput(side, slot, amount.toLong()).toInt()
}