package me.ste.library.container.fabric

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.VanillaResource
import me.ste.library.transaction.fabric.TransactionContextWrapper
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

class ResourceHolderAdapter<T : VanillaResource<*>, V>(
    private val parent: ResourceContainerAdapter<T, V>,
    private val holder: ResourceHolder<T>
) : StorageView<V> {
    override fun extract(resource: V, maxAmount: Long, transaction: TransactionContext): Long {
        val convertedResource = this.parent.reverseConverter.apply(resource)

        if (convertedResource != this.holder.resource) {
            return 0L
        }

        val wrapper = TransactionContextWrapper(transaction)

        return this.holder.output(maxAmount, wrapper)
    }

    override fun isResourceBlank() = this.holder.isEmpty

    override fun getResource() = this.parent.forwardConverter.apply(this.holder.resource)

    override fun getAmount() = this.holder.amount

    override fun getCapacity() = if (this.holder.capacity != -1L) this.holder.capacity else Long.MAX_VALUE
}