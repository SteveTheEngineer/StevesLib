package me.ste.library.transfer.fluid

import me.ste.library.transfer.base.ResourceContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction

interface FluidContainer : ResourceContainer<StackableFluid> {
    fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long // returns accepted amount
    fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long // returns output amount
}