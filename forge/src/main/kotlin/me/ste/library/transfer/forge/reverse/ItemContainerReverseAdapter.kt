package me.ste.library.transfer.forge.reverse

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.item.SimulatableItemContainer
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction
import net.minecraftforge.items.IItemHandler
import java.util.function.Function
import kotlin.math.min

class ItemContainerReverseAdapter(
    private val handlers: Function<Direction?, IItemHandler?>
) : SimulatableItemContainer {
    private fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>, simulate: Boolean): Long {
        val handler = this.handlers.apply(side) ?: return 0L
        val stack = ResourceWithAmount.toItemStack(resource.capToInt())
        val remaining = handler.insertItem(slot, stack, simulate)
        return (stack.count - remaining.count).toLong()
    }

    private fun output(side: Direction?, slot: Int, amount: Long, simulate: Boolean): Long {
        val handler = this.handlers.apply(side) ?: return 0L
        return handler.extractItem(slot, min(amount, Int.MAX_VALUE.toLong()).toInt(), simulate).count.toLong()
    }

    override fun simulateAccept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.accept(side, slot, resource, true)

    override fun simulateOutput(side: Direction?, slot: Int, amount: Long) = this.output(side, slot, amount, true)

    override fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.accept(side, slot, resource, false)

    override fun output(side: Direction?, slot: Int, amount: Long) = this.output(side, slot, amount, false)

    override fun getContainerSize(side: Direction?) = this.handlers.apply(side)?.slots ?: 0

    override fun getResource(side: Direction?, slot: Int): ResourceWithAmount<StackableItem> {
        val handler = this.handlers.apply(side) ?: return ResourceWithAmount.EMPTY_ITEM

        val stack = handler.getStackInSlot(slot)
        if (stack.isEmpty) {
            return ResourceWithAmount.EMPTY_ITEM
        }

        return ResourceWithAmount.fromItemStack(stack)
    }

    override fun getCapacity(side: Direction?, slot: Int) =
        this.handlers.apply(side)?.getSlotLimit(slot)?.toLong() ?: 0L

    override fun canAccept(side: Direction?, slot: Int, resource: StackableItem): Boolean {
        val handler = this.handlers.apply(side) ?: return false
        val stack = resource.toStack(1)
        return handler.isItemValid(slot, stack)
    }

    override fun canAccept(side: Direction?) = this.handlers.apply(side) != null

    override fun canOutput(side: Direction?) = this.handlers.apply(side) != null
}