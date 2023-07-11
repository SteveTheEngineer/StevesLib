package me.ste.library.forge

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraftforge.fluids.FluidStack

object ResourceConversionsForge {
    fun fromForge(stack: FluidStack): StackableFluid {
        if (stack.isEmpty) {
            return StackableFluid.EMPTY
        }

        return StackableFluid(stack.fluid, stack.tag)
    }

    fun fromForgeWithAmount(stack: FluidStack) = ResourceWithAmount(
        this.fromForge(stack), stack.amount.toLong()
    )

    fun toForgeWithAmount(resource: ResourceWithAmount<StackableFluid>): FluidStack {
        if (resource.amount > Int.MAX_VALUE) {
            throw IllegalArgumentException("Resource amount too large! ${resource.amount} > ${Int.MAX_VALUE}")
        }

        return FluidStack(
            resource.resource.fluid, resource.amount.toInt(), resource.resource.tag
        )
    }
}