package me.ste.library.container.resource

import me.ste.library.transaction.TransactionShard

interface SingleSlotResourceContainer<T> : ResourceContainer<T>, ResourceHolder<T> {
    override val slots get() = 1

    override val slot get() = 0

    override fun getSlot(slot: Int) = if (slot == 0) this else null

    override fun getResource(resource: T): ResourceHolder<T>? {
        if (resource != this.resource) {
            return null
        }

        return this
    }

    override fun output(resource: T, amount: Long, transaction: TransactionShard): Long {
        if (resource != this.resource) {
            return 0L
        }

        return this.output(amount, transaction)
    }

    override fun iterator(): Iterator<ResourceHolder<T>> {
        var hasNext = true

        return object : Iterator<ResourceHolder<T>> {
            override fun hasNext() = hasNext
            override fun next(): ResourceHolder<T> {
                hasNext = false
                return this@SingleSlotResourceContainer
            }
        }
    }
}