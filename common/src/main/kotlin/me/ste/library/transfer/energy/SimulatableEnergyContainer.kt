package me.ste.library.transfer.energy

import dev.architectury.fluid.FluidStack
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack

interface SimulatableEnergyContainer : EnergyContainer {
    fun simulateAccept(side: Direction?, energy: Long): Long // returns accepted energy
    fun simulateOutput(side: Direction?, energy: Long): Long // returns output energy
}