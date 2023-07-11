package me.ste.library.simple.conversion

import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.transfer.units.DropletUnits
import net.minecraft.core.Direction

interface DropletSnapshotFluidContainer<S> : SnapshotFluidContainer<S> {
    fun getResourceDroplets(side: Direction?, slot: Int): ResourceWithAmount<StackableFluid>
    fun getCapacityDroplets(side: Direction?, slot: Int): Long

    fun acceptDroplets(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long
    fun outputDroplets(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long

    override fun getResource(side: Direction?, slot: Int) = DropletUnits.toPlatformUnits(
        this.getResourceDroplets(side, slot)
    )

    override fun getCapacity(side: Direction?, slot: Int) = DropletUnits.toPlatformUnits(
        this.getCapacityDroplets(side, slot)
    )

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = DropletUnits.toPlatformUnits(
        this.acceptDroplets(side, DropletUnits.fromPlatformUnits(resource))
    )

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = DropletUnits.toPlatformUnits(
        this.outputDroplets(side, DropletUnits.fromPlatformUnits(resource))
    )
}