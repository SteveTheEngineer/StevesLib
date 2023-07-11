package me.ste.library.transfer.fluid

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction

interface SimulatableFluidContainer : FluidContainer {
    fun simulateAccept(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long // returns accepted amount
    fun simulateOutput(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long // returns output amount
}