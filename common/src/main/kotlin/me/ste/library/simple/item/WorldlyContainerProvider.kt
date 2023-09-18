package me.ste.library.simple.item

import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.core.Direction
import net.minecraft.world.WorldlyContainer

open class WorldlyContainerProvider(
    protected val container: WorldlyContainer,
    protected val side: Direction
) : ResourceContainer<ItemResource> {
    override val slots get() = this.container.getSlotsForFace(this.side).size

    override fun getSlot(slot: Int) = WorldlyContainerHolder(this.container, this.side, this.container.getSlotsForFace(this.side)[slot])

    override val canAccept get() = true

    override val canOutput get() = true

    override fun canOutput(resource: ItemResource) = this.any { it.canOutput(resource) }

    override fun canAccept(resource: ItemResource) = this.any { it.canAccept(resource) }

    override fun output(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
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

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        var remaining = amount

        for (holder in this) {
            if (remaining <= 0L) {
                break
            }

            remaining -= holder.accept(resource, remaining, transaction)
        }

        return amount - remaining
    }

    override fun getResource(resource: ItemResource) = null

    override fun iterator(): Iterator<ResourceHolder<ItemResource>> {
        var slot = 0

        return object : Iterator<ResourceHolder<ItemResource>> {
            override fun hasNext() = slot < this@WorldlyContainerProvider.slots
            override fun next() = this@WorldlyContainerProvider.getSlot(slot++)
        }
    }
}