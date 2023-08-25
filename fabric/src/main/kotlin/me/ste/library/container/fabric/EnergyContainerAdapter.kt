package me.ste.library.container.fabric

import me.ste.library.container.EnergyContainer
import me.ste.library.transaction.fabric.TransactionContextWrapper
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import team.reborn.energy.api.EnergyStorage

class EnergyContainerAdapter(
    val container: EnergyContainer
) : EnergyStorage {
    override fun insert(maxAmount: Long, transaction: TransactionContext): Long {
        val wrapper = TransactionContextWrapper(transaction)
        return this.container.accept(maxAmount, wrapper)
    }

    override fun extract(maxAmount: Long, transaction: TransactionContext): Long {
        val wrapper = TransactionContextWrapper(transaction)
        return this.container.output(maxAmount, wrapper)
    }

    override fun getAmount() = this.container.stored

    override fun getCapacity() = this.container.capacity

    override fun supportsInsertion() = this.container.canAccept

    override fun supportsExtraction() = this.container.canOutput
}