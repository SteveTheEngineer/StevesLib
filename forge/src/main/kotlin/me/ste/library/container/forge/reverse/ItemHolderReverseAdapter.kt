package me.ste.library.container.forge.reverse

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard

class ItemHolderReverseAdapter(
    private val parent: ItemContainerReverseAdapter,
    override val slot: Int
) : ResourceHolder<ItemResource> {
    override val resource: ItemResource get() {
        val stack = this.parent.storage.getStackInSlot(this.slot)

        val resource = ItemResource(stack)
        val slot = this.parent.getSimulatedSlot(this.slot) ?: return resource

        if (stack.count + slot.change <= 0) {
            return ItemResource.EMPTY
        }

        if (slot.change > 0 && resource.isEmpty) {
            return slot.resource
        }

        return resource
    }

    override val amount: Long get() {
        val amount = this.parent.storage.getStackInSlot(this.slot).count
        val slot = this.parent.getSimulatedSlot(this.slot) ?: return amount.toLong()
        return (amount + slot.change).toLong()
    }

    override val capacity get() = this.parent.storage.getSlotLimit(this.slot).toLong()

    override val isEmpty: Boolean get() {
        val stack = this.parent.storage.getStackInSlot(this.slot)

        if (stack.isEmpty) {
            return true
        }

        val slot = this.parent.getSimulatedSlot(this.slot) ?: return false
        return (stack.count + slot.change) <= 0
    }

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        val slot = this.parent.getSimulatedSlot(this.slot, transaction)
        return slot.exchange(amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), resource).toLong()
    }

    override fun output(amount: Long, transaction: TransactionShard): Long {
        val slot = this.parent.getSimulatedSlot(this.slot, transaction)
        return slot.exchange(-(amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()), resource).toLong()
    }

    override val canAccept = true

    override val canOutput = true

    override fun canOutput(resource: ItemResource) = this.canOutput

    override fun canAccept(resource: ItemResource) = this.parent.storage.isItemValid(this.slot, resource.toStack())
}