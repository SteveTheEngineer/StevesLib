package me.ste.library.simple.wrapper

import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.transfer.units.DropletUnits
import net.minecraft.core.Direction

class DropletFluidContainerWrapper(
    private val container: SimulatableFluidContainer
): SimulatableFluidContainer by container {
    override fun getResource(side: Direction?, slot: Int) = DropletUnits.fromPlatformUnits(
        this.container.getResource(side, slot)
    )

    override fun getCapacity(side: Direction?, slot: Int) = DropletUnits.fromPlatformUnits(
        this.container.getCapacity(side, slot)
    )

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = DropletUnits.fromPlatformUnits(
        this.container.accept(side, DropletUnits.toPlatformUnits(resource))
    )

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = DropletUnits.fromPlatformUnits(
        this.container.output(side, DropletUnits.toPlatformUnits(resource))
    )

    override fun simulateAccept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = DropletUnits.fromPlatformUnits(
        this.container.simulateAccept(side, DropletUnits.toPlatformUnits(resource))
    )

    override fun simulateOutput(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = DropletUnits.fromPlatformUnits(
        this.container.simulateOutput(side, DropletUnits.toPlatformUnits(resource))
    )
}