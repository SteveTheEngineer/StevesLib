package me.ste.library.container.fabric

import me.ste.library.container.resource.ResourceContainer
import me.ste.library.resource.VanillaResource
import me.ste.library.transaction.fabric.TransactionContextWrapper
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import java.util.function.Function

class ResourceContainerAdapter<T : VanillaResource<*>, V>(
    val container: ResourceContainer<T>,

    val forwardConverter: Function<T, V>,
    val reverseConverter: Function<V, T>
) : Storage<V> {
    override fun iterator(): MutableIterator<StorageView<V>> {
        val holderIterator = this.container.iterator()

        return object : MutableIterator<StorageView<V>> {
            override fun hasNext() = holderIterator.hasNext()

            override fun next() = ResourceHolderAdapter(this@ResourceContainerAdapter, holderIterator.next())

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    override fun extract(resource: V, maxAmount: Long, transaction: TransactionContext): Long {
        val wrapper = TransactionContextWrapper(transaction)
        val convertedResource = this.reverseConverter.apply(resource)

        return this.container.output(convertedResource, maxAmount, wrapper)
    }

    override fun insert(resource: V, maxAmount: Long, transaction: TransactionContext): Long {
        val wrapper = TransactionContextWrapper(transaction)
        val convertedResource = this.reverseConverter.apply(resource)

        return this.container.accept(convertedResource, maxAmount, wrapper)
    }

    override fun supportsInsertion() = this.container.canAccept

    override fun supportsExtraction() = this.container.canOutput

    override fun exactView(resource: V): StorageView<V>? {
        val convertedResource = this.reverseConverter.apply(resource)
        val holder = this.container.getResource(convertedResource)
            ?: return null

        return ResourceHolderAdapter(this, holder)
    }
}