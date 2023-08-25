package me.ste.library.container.fabric

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.VanillaResource
import me.ste.library.transaction.fabric.TransactionContextWrapper
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

class ResourceHolderAdapter<T : VanillaResource<*>, V>(
    private val parent: ResourceContainerAdapter<T, V>,
    private val resource: ResourceHolder<T>
) : StorageView<V> {
    override fun extract(resource: V, maxAmount: Long, transaction: TransactionContext): Long {
        val convertedResource = this.parent.reverseConverter.apply(resource)

        if (convertedResource != this.resource) {
            return 0L
        }

        val wrapper = TransactionContextWrapper(transaction)

        return this.resource.output(maxAmount, wrapper)
    }

    override fun isResourceBlank() = this.resource.isEmpty

    override fun getResource() = this.parent.forwardConverter.apply(this.resource.resource)

    override fun getAmount() = this.resource.amount

    override fun getCapacity() = if (this.resource.capacity != -1L) this.resource.capacity else Long.MAX_VALUE
}