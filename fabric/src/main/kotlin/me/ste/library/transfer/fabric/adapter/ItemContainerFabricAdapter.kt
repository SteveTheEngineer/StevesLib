package me.ste.library.transfer.fabric.adapter

import me.ste.library.fabric.ResourceConversionsFabric
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.fabric.slot.ItemContainerFabricSlotAdapter
import me.ste.library.transfer.item.SnapshotItemContainer
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.core.Direction

class ItemContainerFabricAdapter(
    private val container: SnapshotItemContainer<*>,
    private val side: Direction?
) : SnapshotParticipant<Any?>(), Storage<ItemVariant> {
    override fun iterator(): MutableIterator<StorageView<ItemVariant>> {
        var currentSlot = 0
        val slots = this.container.getContainerSize(this.side)

        return object : MutableIterator<StorageView<ItemVariant>> {
            override fun hasNext() = currentSlot < slots

            override fun next() = ItemContainerFabricSlotAdapter(
                this@ItemContainerFabricAdapter.container,
                this@ItemContainerFabricAdapter.side,
                currentSlot++
            )

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    override fun extract(variant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)

        val resource = ResourceConversionsFabric.fromFabric(variant)

        var remaining = maxAmount
        for (slot in 0 until this.container.getContainerSize(this.side)) {
            if (remaining <= 0) {
                break
            }

            val containerResource = this.container.getResource(this.side, slot)

            if (containerResource.resource != resource) {
                continue
            }

            remaining -= this.container.output(this.side, slot, remaining)
        }

        return maxAmount - remaining
    }

    override fun insert(variant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)

        val resource = ResourceConversionsFabric.fromFabric(variant)

        var remaining = maxAmount
        for (slot in 0 until this.container.getContainerSize(this.side)) {
            if (remaining <= 0) {
                break
            }

            remaining -= this.container.accept(this.side, slot, ResourceWithAmount(resource, remaining))
        }

        return maxAmount - remaining
    }

    override fun supportsExtraction() = this.container.canOutput(this.side)

    override fun supportsInsertion() = this.container.canAccept(this.side)

    override fun createSnapshot() = this.container.createSnapshot(this.side)

    override fun readSnapshot(snapshot: Any?) {
        SnapshotUtils.readSnapshot(this.container, this.side, snapshot)
    }

    override fun onFinalCommit() {
        this.container.saveChanges(this.side)
    }
}