package me.ste.library.transfer.fabric.slot

import me.ste.library.transfer.fabric.slot.FluidContainerFabricSlotAdapter
import me.ste.library.transfer.fluid.FluidContainerItem
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

class FluidContainerFabricItemSlotAdapter(private val context: ContainerItemContext, private val itemContainer: FluidContainerItem<*>, slot: Int) : FluidContainerFabricSlotAdapter(itemContainer, null, slot) {
    override fun extract(variant: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long {
        val result = super.extract(variant, maxAmount, transaction)

        val stack = this.itemContainer.getResult()
        this.context.exchange(ItemVariant.of(stack), stack.count.toLong(), transaction)

        return result
    }
}