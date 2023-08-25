package me.ste.library.container.slotted

import dev.architectury.fluid.FluidStack
import me.ste.library.resource.FluidResource
import me.ste.library.resource.QuantifiedResource
import me.ste.library.unit.DropletUnits

abstract class DropletsSlottedFluidContainer : SlottedFluidContainer {
    // Abstracts
    abstract fun getFluidDroplets(slot: Int): QuantifiedResource<FluidResource>
    abstract fun setFluidDroplets(slot: Int, resource: QuantifiedResource<FluidResource>)

    abstract fun canPlace(slot: Int, resource: FluidResource): Boolean
    abstract fun canTake(slot: Int, resource: FluidResource): Boolean

    abstract fun getCapacityDroplets(slot: Int): Long

    // Implementation
    final override fun getFluid(slot: Int): FluidStack {
        val resource = this.getFluidDroplets(slot)
        val platformAmount = DropletUnits.toPlatformUnits(resource.amount)

        return resource.resource.toStack(platformAmount)
    }

    final override fun setFluid(slot: Int, stack: FluidStack) {
        val dropletsAmount = DropletUnits.fromPlatformUnits(stack.amount)
        val resource = QuantifiedResource(FluidResource(stack), dropletsAmount)

        this.setFluidDroplets(slot, resource)
    }

    final override fun canPlace(slot: Int, stack: FluidStack) =
        this.canPlace(slot, FluidResource(stack))

    final override fun canTake(slot: Int, stack: FluidStack) =
        this.canTake(slot, FluidResource(stack))

    final override fun getCapacity(slot: Int) =
        DropletUnits.toPlatformUnits(this.getCapacityDroplets(slot))
}