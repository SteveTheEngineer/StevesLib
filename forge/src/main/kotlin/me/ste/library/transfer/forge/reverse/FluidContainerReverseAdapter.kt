package me.ste.library.transfer.forge.reverse

import me.ste.library.forge.ResourceConversionsForge
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction
import net.minecraftforge.fluids.capability.IFluidHandler
import java.util.function.Function

open class FluidContainerReverseAdapter(
    private val handlers: Function<Direction?, IFluidHandler?>
) : SimulatableFluidContainer {
    private fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>, simulate: Boolean): Long {
        val handler = this.handlers.apply(side) ?: return 0L
        val stack = ResourceConversionsForge.toForgeWithAmount(resource.capToInt())
        return handler.fill(stack, if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE).toLong()
    }

    private fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>, simulate: Boolean): Long {
        val handler = this.handlers.apply(side) ?: return 0L
        val stack = ResourceConversionsForge.toForgeWithAmount(resource.capToInt())
        return handler.drain(stack, if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE).amount.toLong()
    }

    override fun simulateAccept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.accept(side, resource, true)

    override fun simulateOutput(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.output(side, resource, true)

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.accept(side, resource, false)

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.output(side, resource, false)

    override fun getContainerSize(side: Direction?) = this.handlers.apply(side)?.tanks ?: 0

    override fun getResource(side: Direction?, slot: Int): ResourceWithAmount<StackableFluid> {
        val handler = this.handlers.apply(side) ?: return ResourceWithAmount.EMPTY_FLUID

        val stack = handler.getFluidInTank(slot)
        if (stack.isEmpty) {
            return ResourceWithAmount.EMPTY_FLUID
        }

        return ResourceConversionsForge.fromForgeWithAmount(stack)
    }

    override fun getCapacity(side: Direction?, slot: Int) = this.handlers.apply(side)?.getTankCapacity(slot)?.toLong() ?: 0L

    override fun canAccept(side: Direction?, slot: Int, resource: StackableFluid): Boolean {
        val handler = this.handlers.apply(side) ?: return false

        val stack = ResourceConversionsForge.toForgeWithAmount(
            ResourceWithAmount(resource, 1000L)
        )

        return handler.isFluidValid(slot, stack)
    }

    override fun canAccept(side: Direction?) = this.handlers.apply(side) != null

    override fun canOutput(side: Direction?) = this.handlers.apply(side) != null
}