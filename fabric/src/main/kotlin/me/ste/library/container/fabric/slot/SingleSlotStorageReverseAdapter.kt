package me.ste.library.container.fabric.slot

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import me.ste.library.transaction.fabric.TransactionsImpl
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage

class SingleSlotStorageReverseAdapter(
    private val storage: SingleSlotStorage<ItemVariant>
) : ResourceHolder<ItemResource> {
    override val resource = ItemResource(this.storage.resource.item, this.storage.resource.nbt)

    override val amount = this.storage.amount

    override val capacity = this.storage.capacity

    override val isEmpty = this.storage.isResourceBlank || this.storage.amount <= 0L

    override val slot = 0

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        return this.storage.insert(ItemVariant.of(resource.obj, resource.tag), amount, TransactionsImpl.getContext(transaction))
    }

    override fun output(amount: Long, transaction: TransactionShard): Long {
        return this.storage.extract(this.storage.resource, amount, TransactionsImpl.getContext(transaction))
    }

    override val canAccept = this.storage.supportsInsertion()

    override val canOutput = this.storage.supportsExtraction()

    override fun canOutput(resource: ItemResource) = this.canOutput

    override fun canAccept(resource: ItemResource) = this.canAccept
}