package me.ste.library.container.fabric.reverse

import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.VanillaResource
import me.ste.library.transaction.TransactionShard
import me.ste.library.transaction.fabric.TransactionsImpl
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import java.util.function.Function

class ResourceContainerReverseAdapter<T : VanillaResource<*>, V>(
    private val storage: Storage<V>,

    val forwardConverter: Function<V, T>,
    val reverseConverter: Function<T, V>
) : ResourceContainer<T> {
    override val slots = -1

    override fun getSlot(slot: Int) = null

    override val canAccept get() = this.storage.supportsInsertion()

    override val canOutput get() = this.storage.supportsExtraction()

    override fun canAccept(resource: T) = this.canAccept

    override fun canOutput(resource: T) = this.canOutput

    override fun accept(resource: T, amount: Long, transaction: TransactionShard): Long {
        if (resource.isEmpty) {
            return 0L
        }

        val nativeTransaction = TransactionsImpl.getContext(transaction)
        val nativeResource = this.reverseConverter.apply(resource)

        return this.storage.insert(nativeResource, amount, nativeTransaction)
    }

    override fun output(resource: T, amount: Long, transaction: TransactionShard): Long {
        if (resource.isEmpty) {
            return 0L
        }

        val nativeTransaction = TransactionsImpl.getContext(transaction)
        val nativeResource = this.reverseConverter.apply(resource)

        return this.storage.extract(nativeResource, amount, nativeTransaction)
    }

    override fun getResource(resource: T): ResourceHolder<T>? {
        val nativeResource = this.reverseConverter.apply(resource)
        val view = this.storage.exactView(nativeResource) ?: return null
        return ResourceHolderReverseAdapter(this, view)
    }

    override fun iterator(): Iterator<ResourceHolder<T>> {
        val viewIterator = this.storage.iterator()

        return object : Iterator<ResourceHolder<T>> {
            override fun hasNext() = viewIterator.hasNext()

            override fun next() = ResourceHolderReverseAdapter(this@ResourceContainerReverseAdapter, viewIterator.next())
        }
    }
}