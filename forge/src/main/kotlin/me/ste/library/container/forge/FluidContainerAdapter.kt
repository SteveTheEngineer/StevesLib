package me.ste.library.container.forge

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.resource.FluidResource
import me.ste.library.transaction.Transactions
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

open class FluidContainerAdapter(
    val container: ResourceContainer<FluidResource>
) : IFluidHandler {
    private val usesSlots get() = this.container.slots != -1

    override fun getTanks(): Int {
        val slots = this.container.slots

        if (slots == -1) {
            return this.container.count()
        }

        return slots
    }

    override fun getFluidInTank(slot: Int): FluidStack {
        val holder = if (this.usesSlots) {
            this.container.getSlot(slot)
        } else {
            this.container.elementAtOrNull(slot)
        } ?: return FluidStack.EMPTY

        return FluidStack(holder.resource.obj, holder.amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), holder.resource.tag)
    }

    override fun getTankCapacity(slot: Int): Int {
        val holder = if (this.usesSlots) {
            this.container.getSlot(slot)
        } else {
            this.container.elementAtOrNull(slot)
        } ?: return 0

        return holder.capacity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
        val resource = FluidResource(
            FluidStackHooksForge.fromForge(stack)
        )

        val holder = if (this.usesSlots) {
            this.container.getSlot(slot)
        } else {
            this.container.elementAtOrNull(slot)
        } ?: return this.container.canAccept(
            resource
        )

        return holder.canAccept(resource)
    }

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        val resource = FluidResource(
            FluidStackHooksForge.fromForge(stack)
        )

        var accepted = 0

        Transactions.open {
            accepted = this.container.accept(resource, stack.amount.toLong(), it).toInt()

            if (action == IFluidHandler.FluidAction.EXECUTE) {
                it.keep()
            }
        }

        return accepted
    }

    override fun drain(stack: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        val resource = FluidResource(
            FluidStackHooksForge.fromForge(stack)
        )

        var output = 0

        Transactions.open {
            output = this.container.output(resource, stack.amount.toLong(), it).toInt()

            if (action == IFluidHandler.FluidAction.EXECUTE) {
                it.keep()
            }
        }

        val result = stack.copy()
        result.amount = output
        return result
    }

    override fun drain(amount: Int, action: IFluidHandler.FluidAction): FluidStack {
        val holder = this.container.first { it.canOutput && !it.isEmpty }
        val resource = holder.resource

        var output = 0

        Transactions.open {
            output = holder.output(amount.toLong(), it).toInt()

            if (action == IFluidHandler.FluidAction.EXECUTE) {
                it.keep()
            }
        }

        return FluidStack(resource.obj, output, resource.tag)
    }

}