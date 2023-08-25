package me.ste.library.simple.slot.fluid

import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.container.slotted.SlottedFluidContainer
import me.ste.library.resource.FluidResource
import me.ste.library.transaction.TransactionShard

open class SlottedFluidContainerProvider(
    private val container: SlottedFluidContainer
) : ResourceContainer<FluidResource> {
    override val slots get() = this.container.size

    override fun getSlot(slot: Int) = SlottedFluidContainerHolder(this.container, slot)

    override val canAccept get() = true

    override val canOutput get() = true

    override fun canOutput(resource: FluidResource) = this.any { it.canOutput(resource) }

    override fun canAccept(resource: FluidResource) = this.any { it.canAccept(resource) }

    override fun output(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        var remaining = amount

        for (holder in this) {
            if (remaining <= 0L) {
                break
            }

            if (holder.isEmpty || !holder.resource.isSame(resource)) {
                continue
            }

            remaining -= holder.output(remaining, transaction)
        }

        return amount - remaining
    }

    override fun accept(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        var remaining = amount

        for (holder in this) {
            if (remaining <= 0L) {
                break
            }

            remaining -= holder.accept(resource, remaining, transaction)
        }

        return amount - remaining
    }

    override fun getResource(resource: FluidResource) = null

    override fun iterator(): Iterator<ResourceHolder<FluidResource>> {
        var slot = 0

        return object : Iterator<ResourceHolder<FluidResource>> {
            override fun hasNext() = slot < this@SlottedFluidContainerProvider.slots
            override fun next() = this@SlottedFluidContainerProvider.getSlot(slot++)
        }
    }
}