package me.ste.library.container.forge.reverse

import me.ste.library.container.forge.simulation.SimulatedItemSlot
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard
import net.minecraftforge.items.IItemHandler

class ItemContainerReverseAdapter(
    val storage: IItemHandler
) : ResourceContainer<ItemResource> {
    private var simulatedSlots = mutableMapOf<Int, SimulatedItemSlot>()
    private val snapshots = mutableMapOf<Int, MutableMap<Int, SimulatedItemSlot>>()

    fun getSimulatedSlot(slot: Int, transaction: TransactionShard): SimulatedItemSlot {
        this.track(transaction)
        return this.simulatedSlots.computeIfAbsent(slot) { SimulatedItemSlot(this.storage, it) }
    }

    fun getSimulatedSlot(slot: Int) = this.simulatedSlots[slot]

    private fun track(transaction: TransactionShard) {
        if (transaction.depth in this.snapshots) {
            return
        }

        transaction.onEnd {
            if (it == TransactionResult.REVERT) {
                this.simulatedSlots = this.snapshots[transaction.depth]!!
            }

            this.snapshots -= transaction.depth
        }

        if (this.snapshots.isNotEmpty()) {
            return
        }

        transaction.onFinalEnd {
            if (it != TransactionResult.KEEP) {
                this.simulatedSlots.clear()
                return@onFinalEnd
            }

            for ((_, slot) in this.simulatedSlots) {
                slot.keep()
            }

            this.simulatedSlots.clear()
        }

        this.snapshots[transaction.depth] = this.simulatedSlots.mapValues { (_, value) -> value.copy() }.toMutableMap()
    }

    override val slots get() = this.storage.slots

    override fun getSlot(slot: Int) = ItemHolderReverseAdapter(this, slot)

    override val canAccept get() = this.storage.slots > 0

    override val canOutput get() = this.storage.slots > 0

    override fun canOutput(resource: ItemResource) = this.canOutput

    override fun canAccept(resource: ItemResource): Boolean {
        if (!this.canAccept) {
            return false
        }

        for (slot in 0 until this.storage.slots) {
            if (!this.storage.isItemValid(slot, resource.toStack())) {
                return false
            }
        }

        return true
    }

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        var remaining = amount

        for (index in 0 until this.slots) {
            if (remaining <= 0) {
                break
            }

            val slot = this.getSimulatedSlot(index, transaction)
            remaining -= slot.exchange(remaining.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), resource)
        }

        return amount - remaining
    }

    override fun output(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        var remaining = amount

        for (index in 0 until this.slots) {
            if (remaining <= 0) {
                break
            }

            val slot = this.getSimulatedSlot(index, transaction)
            remaining -= slot.exchange(-(remaining.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()), resource)
        }

        return amount - remaining
    }

    override fun getResource(resource: ItemResource) = null

    override fun iterator(): Iterator<ResourceHolder<ItemResource>> {
        var slot = 0

        return object : Iterator<ResourceHolder<ItemResource>> {
            override fun hasNext() = slot < this@ItemContainerReverseAdapter.storage.slots

            override fun next() = ItemHolderReverseAdapter(this@ItemContainerReverseAdapter, slot++)
        }
    }

}