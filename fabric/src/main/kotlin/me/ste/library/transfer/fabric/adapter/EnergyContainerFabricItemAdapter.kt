package me.ste.library.transfer.fabric.adapter

import me.ste.library.transfer.energy.EnergyContainerItem
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

class EnergyContainerFabricItemAdapter(private val context: ContainerItemContext, private val itemContainer: EnergyContainerItem<*>) : EnergyContainerFabricAdapter(itemContainer, null) {
    override fun insert(energy: Long, transaction: TransactionContext): Long {
        val result = super.insert(energy, transaction)

        val stack = this.context.itemVariant.toStack(this.context.amount.toInt())
        this.itemContainer.applyResult(stack)
        this.context.exchange(ItemVariant.of(stack), stack.count.toLong(), transaction)

        return result
    }

    override fun extract(energy: Long, transaction: TransactionContext): Long {
        val result = super.extract(energy, transaction)

        val stack = this.context.itemVariant.toStack(this.context.amount.toInt())
        this.itemContainer.applyResult(stack)
        this.context.exchange(ItemVariant.of(stack), stack.count.toLong(), transaction)

        return result
    }
}