package me.ste.library.simple.container.fluid

import me.ste.library.simple.conversion.MillibucketSnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction

open class MillibucketSingleSlotFluidContainer(
    capacity: Long,
    maxAccept: Long,
    maxOutput: Long,
    setChanged: Runnable = Runnable {}
) : SingleSlotFluidContainer(capacity, maxAccept, maxOutput, setChanged), MillibucketSnapshotFluidContainer<ResourceWithAmount<StackableFluid>> {
    override fun getResourceMillibuckets(side: Direction?, slot: Int) = super<SingleSlotFluidContainer>.getResource(side, slot)

    override fun getCapacityMillibuckets(side: Direction?, slot: Int) = super<SingleSlotFluidContainer>.getCapacity(side, slot)

    override fun acceptMillibuckets(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<SingleSlotFluidContainer>.accept(side, resource)

    override fun outputMillibuckets(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<SingleSlotFluidContainer>.output(side, resource)

    override fun getResource(side: Direction?, slot: Int) = super<MillibucketSnapshotFluidContainer>.getResource(side, slot)

    override fun getCapacity(side: Direction?, slot: Int) = super<MillibucketSnapshotFluidContainer>.getCapacity(side, slot)

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<MillibucketSnapshotFluidContainer>.accept(side, resource)

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<MillibucketSnapshotFluidContainer>.output(side, resource)
}