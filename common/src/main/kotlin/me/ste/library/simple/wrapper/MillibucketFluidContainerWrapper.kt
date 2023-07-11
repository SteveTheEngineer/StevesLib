package me.ste.library.simple.wrapper

import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.transfer.units.MillibucketUnits
import net.minecraft.core.Direction

class MillibucketFluidContainerWrapper(
    private val container: SimulatableFluidContainer
): SimulatableFluidContainer by container {
    override fun getResource(side: Direction?, slot: Int) = MillibucketUnits.fromPlatformUnits(
        this.container.getResource(side, slot)
    )

    override fun getCapacity(side: Direction?, slot: Int) = MillibucketUnits.fromPlatformUnits(
        this.container.getCapacity(side, slot)
    )

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = MillibucketUnits.fromPlatformUnits(
        this.container.accept(side, MillibucketUnits.toPlatformUnits(resource))
    )

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = MillibucketUnits.fromPlatformUnits(
        this.container.output(side, MillibucketUnits.toPlatformUnits(resource))
    )

    override fun simulateAccept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = MillibucketUnits.fromPlatformUnits(
        this.container.simulateAccept(side, MillibucketUnits.toPlatformUnits(resource))
    )

    override fun simulateOutput(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = MillibucketUnits.fromPlatformUnits(
        this.container.simulateOutput(side, MillibucketUnits.toPlatformUnits(resource))
    )
}