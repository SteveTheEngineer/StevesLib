package me.ste.library.transfer.fabric.slot

import me.ste.library.fabric.ResourceConversionsFabric
import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.item.SnapshotItemContainer
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.core.Direction

class ItemContainerFabricSlotAdapter(
    private val container: SnapshotItemContainer<*>,
    private val side: Direction?,
    private val slot: Int
) : SnapshotParticipant<Any?>(), StorageView<ItemVariant> {
    override fun extract(variant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)

        val currentResource = this.container.getResource(this.side, this.slot).resource
        val resource = ResourceConversionsFabric.fromFabric(variant)

        if (currentResource.isEmpty || resource != currentResource) {
            return 0L
        }

        return this.container.output(this.side, this.slot, maxAmount)
    }

    override fun isResourceBlank() = this.container.getResource(this.side, this.slot).resource.isEmpty

    override fun getResource() = ResourceConversionsFabric.toFabricVariant(
        this.container.getResource(this.side, this.slot).resource
    )

    override fun getAmount() = this.container.getResource(this.side, this.slot).amount

    override fun getCapacity() = this.container.getCapacity(this.side, this.slot)

    override fun createSnapshot() = this.container.createSnapshot(this.side)

    override fun readSnapshot(snapshot: Any?) {
        SnapshotUtils.readSnapshot(this.container, this.side, snapshot)
    }

    override fun onFinalCommit() {
        this.container.saveChanges(this.side)
    }
}