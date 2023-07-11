package me.ste.library.simple.container.fluid

import me.ste.library.simple.conversion.DropletSnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction

open class DropletSingleSlotFluidContainer(
    capacity: Long,
    maxAccept: Long,
    maxOutput: Long,
    setChanged: Runnable = Runnable {}
) : SingleSlotFluidContainer(capacity, maxAccept, maxOutput, setChanged), DropletSnapshotFluidContainer<ResourceWithAmount<StackableFluid>> {
    override fun getResourceDroplets(side: Direction?, slot: Int) = super<SingleSlotFluidContainer>.getResource(side, slot)

    override fun getCapacityDroplets(side: Direction?, slot: Int) = super<SingleSlotFluidContainer>.getCapacity(side, slot)

    override fun acceptDroplets(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<SingleSlotFluidContainer>.accept(side, resource)

    override fun outputDroplets(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<SingleSlotFluidContainer>.output(side, resource)

    override fun getResource(side: Direction?, slot: Int) = super<DropletSnapshotFluidContainer>.getResource(side, slot)

    override fun getCapacity(side: Direction?, slot: Int) = super<DropletSnapshotFluidContainer>.getCapacity(side, slot)

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<DropletSnapshotFluidContainer>.accept(side, resource)

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = super<DropletSnapshotFluidContainer>.output(side, resource)
}