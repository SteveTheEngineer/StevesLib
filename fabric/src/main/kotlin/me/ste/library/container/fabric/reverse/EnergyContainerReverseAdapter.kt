package me.ste.library.container.fabric.reverse

import me.ste.library.container.EnergyContainer
import me.ste.library.transaction.TransactionShard
import me.ste.library.transaction.fabric.TransactionsImpl
import team.reborn.energy.api.EnergyStorage

class EnergyContainerReverseAdapter(
    private val storage: EnergyStorage
) : EnergyContainer {
    override val stored get() = this.storage.amount

    override val capacity get() = this.storage.capacity

    override fun accept(energy: Long, transaction: TransactionShard): Long {
        val nativeTransaction = TransactionsImpl.getContext(transaction)
        return this.storage.insert(energy, nativeTransaction)
    }

    override fun output(energy: Long, transaction: TransactionShard): Long {
        val nativeTransaction = TransactionsImpl.getContext(transaction)
        return this.storage.extract(energy, nativeTransaction)
    }

    override val canAccept get() = this.storage.supportsInsertion()
    override val canOutput get() = this.storage.supportsExtraction()

}