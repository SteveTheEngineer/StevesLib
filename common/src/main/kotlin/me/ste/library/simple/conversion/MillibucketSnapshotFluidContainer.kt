package me.ste.library.simple.conversion

import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.transfer.units.MillibucketUnits
import net.minecraft.core.Direction

interface MillibucketSnapshotFluidContainer<S> : SnapshotFluidContainer<S> {
    fun getResourceMillibuckets(side: Direction?, slot: Int): ResourceWithAmount<StackableFluid>
    fun getCapacityMillibuckets(side: Direction?, slot: Int): Long

    fun acceptMillibuckets(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long
    fun outputMillibuckets(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long

    override fun getResource(side: Direction?, slot: Int) = MillibucketUnits.toPlatformUnits(
        this.getResourceMillibuckets(side, slot)
    )

    override fun getCapacity(side: Direction?, slot: Int) = MillibucketUnits.toPlatformUnits(
        this.getCapacityMillibuckets(side, slot)
    )

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = MillibucketUnits.toPlatformUnits(
        this.acceptMillibuckets(side, MillibucketUnits.fromPlatformUnits(resource))
    )

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = MillibucketUnits.toPlatformUnits(
        this.outputMillibuckets(side, MillibucketUnits.fromPlatformUnits(resource))
    )
}