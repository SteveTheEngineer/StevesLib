package me.ste.library.container.slotted

import dev.architectury.fluid.FluidStack
import me.ste.library.resource.FluidResource
import me.ste.library.resource.QuantifiedResource
import me.ste.library.unit.MillibucketUnits

abstract class MillibucketsSlottedFluidContainer : SlottedFluidContainer {
    // Abstracts
    abstract fun getFluidMillibuckets(slot: Int): QuantifiedResource<FluidResource>
    abstract fun setFluidMillibuckets(slot: Int, resource: QuantifiedResource<FluidResource>)

    abstract fun canPlace(slot: Int, resource: FluidResource): Boolean
    abstract fun canTake(slot: Int, resource: FluidResource): Boolean

    abstract fun getCapacityMillibuckets(slot: Int): Long

    // Implementation
    final override fun getFluid(slot: Int): FluidStack {
        val resource = this.getFluidMillibuckets(slot)
        val platformAmount = MillibucketUnits.toPlatformUnits(resource.amount)

        return resource.resource.toStack(platformAmount)
    }

    final override fun setFluid(slot: Int, stack: FluidStack) {
        val millibucketsAmount = MillibucketUnits.fromPlatformUnits(stack.amount)
        val resource = QuantifiedResource(FluidResource(stack), millibucketsAmount)

        this.setFluidMillibuckets(slot, resource)
    }

    final override fun canPlace(slot: Int, stack: FluidStack) =
        this.canPlace(slot, FluidResource(stack))

    final override fun canTake(slot: Int, stack: FluidStack) =
        this.canTake(slot, FluidResource(stack))

    final override fun getCapacity(slot: Int) =
        MillibucketUnits.toPlatformUnits(this.getCapacityMillibuckets(slot))
}