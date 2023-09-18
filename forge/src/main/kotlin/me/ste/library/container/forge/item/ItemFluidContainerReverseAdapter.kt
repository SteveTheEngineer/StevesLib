package me.ste.library.container.forge.item

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import me.ste.library.container.forge.reverse.FluidContainerReverseAdapter
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem

class ItemFluidContainerReverseAdapter(private val itemStorage: IFluidHandlerItem, private val holder: ResourceHolder<ItemResource>) : FluidContainerReverseAdapter(itemStorage) {
    override fun accept(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        val accepted = super.accept(resource, amount, transaction)

        val stack = this.itemStorage.container.copy()
        val simulationStorage = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElseThrow(::AssertionError)

        val fluidStack = FluidStackHooksForge.toForge(
            resource.toStack(accepted)
        )

        simulationStorage.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE)
        val newStack = simulationStorage.container
        simulationStorage.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE)

        this.holder.trySetResource(ItemResource(newStack), newStack.count.toLong(), transaction)

        return accepted
    }

    override fun output(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        val output = super.output(resource, amount, transaction)

        val stack = this.itemStorage.container.copy()
        val simulationStorage = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElseThrow(::AssertionError)

        val fluidStack = FluidStackHooksForge.toForge(
            resource.toStack(output)
        )

        simulationStorage.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE)
        val newStack = simulationStorage.container
        simulationStorage.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE)

        this.holder.trySetResource(ItemResource(newStack), newStack.count.toLong(), transaction)

        return output
    }
}