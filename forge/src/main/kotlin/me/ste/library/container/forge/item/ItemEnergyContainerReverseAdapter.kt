package me.ste.library.container.forge.item

import me.ste.library.container.forge.reverse.EnergyContainerReverseAdapter
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.energy.IEnergyStorage

class ItemEnergyContainerReverseAdapter(storage: IEnergyStorage, private val stack: ItemStack, private val holder: ResourceHolder<ItemResource>) : EnergyContainerReverseAdapter(storage) {
    override fun accept(energy: Long, transaction: TransactionShard): Long {
        val accepted = super.accept(energy, transaction)

        val stack = this.stack.copy()
        val simulationStorage = stack.getCapability(ForgeCapabilities.ENERGY).orElseThrow(::AssertionError)

        simulationStorage.receiveEnergy(accepted.toInt(), false)
        val newStack = stack.copy()
        simulationStorage.extractEnergy(accepted.toInt(), false)

        this.holder.trySetResource(ItemResource(newStack), newStack.count.toLong(), transaction)

        return accepted
    }

    override fun output(energy: Long, transaction: TransactionShard): Long {
        val output = super.output(energy, transaction)

        val stack = this.stack.copy()
        val simulationStorage = stack.getCapability(ForgeCapabilities.ENERGY).orElseThrow(::AssertionError)

        simulationStorage.extractEnergy(output.toInt(), false)
        val newStack = stack.copy()
        simulationStorage.receiveEnergy(output.toInt(), false)

        this.holder.trySetResource(ItemResource(newStack), newStack.count.toLong(), transaction)

        return output
    }
}