package me.ste.library.resource.forge

import dev.architectury.utils.Amount
import me.ste.library.resource.FluidResource
import me.ste.library.resource.QuantifiedResource
import net.minecraftforge.fluids.FluidStack

object ResourceConversionsForge {
    fun toFluidStack(resource: QuantifiedResource<FluidResource>) = toFluidStack(resource.resource, Amount.toInt(resource.amount))
    fun toFluidStack(resource: FluidResource, amount: Int) = FluidStack(resource.obj, amount, resource.tag)

    fun getResource(stack: FluidStack) = FluidResource(stack.fluid, stack.tag)
    fun fromFluidStack(stack: FluidStack) = QuantifiedResource(getResource(stack), stack.amount.toLong())
}