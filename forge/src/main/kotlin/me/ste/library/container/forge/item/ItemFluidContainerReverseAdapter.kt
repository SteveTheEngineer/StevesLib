package me.ste.library.container.forge.item

import me.ste.library.container.forge.reverse.FluidContainerReverseAdapter
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraftforge.fluids.capability.IFluidHandlerItem

class ItemFluidContainerReverseAdapter(private val itemStorage: IFluidHandlerItem, private val holder: ResourceHolder<ItemResource>) : FluidContainerReverseAdapter(itemStorage) {
    override fun accept(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        val accepted = super.accept(resource, amount, transaction)

        val stack = this.itemStorage.container
        this.holder.trySetResource(ItemResource(stack), stack.count.toLong(), transaction)

        return accepted
    }

    override fun output(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        val output = super.output(resource, amount, transaction)

        val stack = this.itemStorage.container
        this.holder.trySetResource(ItemResource(stack), stack.count.toLong(), transaction)

        return output
    }
}