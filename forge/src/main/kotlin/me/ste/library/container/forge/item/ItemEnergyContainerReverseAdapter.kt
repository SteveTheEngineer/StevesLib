package me.ste.library.container.forge.item

import me.ste.library.container.forge.reverse.EnergyContainerReverseAdapter
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.world.item.ItemStack
import net.minecraftforge.energy.IEnergyStorage

class ItemEnergyContainerReverseAdapter(storage: IEnergyStorage, private val stack: ItemStack, private val holder: ResourceHolder<ItemResource>) : EnergyContainerReverseAdapter(storage) {
    override fun accept(energy: Long, transaction: TransactionShard): Long {
        val accepted = super.accept(energy, transaction)
        this.holder.trySetResource(ItemResource(this.stack), this.stack.count.toLong(), transaction)
        return accepted
    }

    override fun output(energy: Long, transaction: TransactionShard): Long {
        val output = super.output(energy, transaction)
        this.holder.trySetResource(ItemResource(this.stack), this.stack.count.toLong(), transaction)
        return output
    }
}