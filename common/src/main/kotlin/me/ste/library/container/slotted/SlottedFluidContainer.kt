package me.ste.library.container.slotted

import dev.architectury.fluid.FluidStack
import net.minecraft.world.item.ItemStack

interface SlottedFluidContainer {
    val size: Int
    val isEmpty: Boolean

    fun getFluid(slot: Int): FluidStack
    fun setFluid(slot: Int, stack: FluidStack)

    fun canPlace(slot: Int, stack: FluidStack): Boolean
    fun canTake(slot: Int, stack: FluidStack): Boolean

    fun getCapacity(slot: Int): Long
}