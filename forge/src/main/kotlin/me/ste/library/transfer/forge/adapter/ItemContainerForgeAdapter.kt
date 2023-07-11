package me.ste.library.transfer.forge.adapter

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.item.SnapshotItemContainer
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import kotlin.math.min

class ItemContainerForgeAdapter(
    private val container: SnapshotItemContainer<*>,
    private val side: Direction?
) : IItemHandler {
    override fun getSlots() = this.container.getContainerSize(this.side)

    override fun getStackInSlot(slot: Int): ItemStack {
        val resource = this.container.getResource(this.side, slot)
        return resource.resource.toStack(
            min(
                resource.amount,
                Int.MAX_VALUE.toLong()
            ).toInt()
        )
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val resource = ResourceWithAmount.from(stack)

        val accepted = SnapshotUtils.simulate(this.container, this.side, simulate) {
            it.accept(this.side, slot, resource)
        }

        if (accepted > resource.amount) {
            throw IllegalStateException("Accepted resource amount is greater than pushed.")
        }

        val remaining = stack.copy()
        remaining.count -= accepted.toInt()
        return remaining
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val resource = this.container.getResource(this.side, slot)

        val output = SnapshotUtils.simulate(this.container, this.side, simulate) {
            it.output(this.side, slot, amount.toLong())
        }

        if (output > amount) {
            throw IllegalStateException("Output resource amount is greater than pulled.")
        }

        return resource.resource.toStack(output.toInt())
    }

    override fun getSlotLimit(slot: Int) = min(
        this.container.getCapacity(this.side, slot),
        Int.MIN_VALUE.toLong()
    ).toInt()

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        val resource = StackableItem(stack)
        return this.container.canAccept(this.side, slot, resource)
    }
}