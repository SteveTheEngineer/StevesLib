package me.ste.library.container.fabric.reverse

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.VanillaResource
import me.ste.library.transaction.TransactionShard
import me.ste.library.transaction.fabric.TransactionsImpl
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView

class ResourceHolderReverseAdapter<T : VanillaResource<*>, V>(
    private val parent: ResourceContainerReverseAdapter<T, V>,
    private val view: StorageView<V>
) : ResourceHolder<T> {
    override val resource get() = this.parent.forwardConverter.apply(this.view.resource)

    override val amount get() = this.view.amount

    override val capacity get() = this.view.capacity

    override val isEmpty get() = this.view.isResourceBlank || this.view.amount == 0L

    override val slot get() = -1

    override fun accept(resource: T, amount: Long, transaction: TransactionShard) = 0L

    override fun output(amount: Long, transaction: TransactionShard): Long {
        val nativeTransaction = TransactionsImpl.getContext(transaction)
        return this.view.extract(this.view.resource, amount, nativeTransaction)
    }

    override val canAccept get() = false
    override val canOutput get() = this.parent.canOutput

    override fun canOutput(resource: T) = this.canOutput

    override fun canAccept(resource: T) = this.canAccept
}