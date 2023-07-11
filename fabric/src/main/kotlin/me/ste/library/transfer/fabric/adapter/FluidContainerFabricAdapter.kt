package me.ste.library.transfer.fabric.adapter

import me.ste.library.fabric.ResourceConversionsFabric
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.fabric.slot.FluidContainerFabricSlotAdapter
import me.ste.library.transfer.fluid.SnapshotFluidContainer
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.core.Direction

open class FluidContainerFabricAdapter(
    protected val container: SnapshotFluidContainer<*>,
    protected val side: Direction?
) : SnapshotParticipant<Any?>(), Storage<FluidVariant> {
    protected open fun createSlotAdapter(slot: Int) = FluidContainerFabricSlotAdapter(
        this.container,
        this.side,
        slot
    )

    override fun iterator(): MutableIterator<StorageView<FluidVariant>> {
        var currentSlot = 0
        val slots = this.container.getContainerSize(this.side)

        return object : MutableIterator<StorageView<FluidVariant>> {
            override fun hasNext() = currentSlot < slots

            override fun next() = this@FluidContainerFabricAdapter.createSlotAdapter(currentSlot++)

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    override fun extract(variant: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)

        val resource = ResourceWithAmount(
            ResourceConversionsFabric.fromFabric(variant), maxAmount
        )

        return this.container.output(this.side, resource)
    }

    override fun insert(variant: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)

        val resource = ResourceWithAmount(
            ResourceConversionsFabric.fromFabric(variant), maxAmount
        )

        return this.container.accept(this.side, resource)
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