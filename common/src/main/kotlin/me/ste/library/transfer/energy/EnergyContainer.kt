package me.ste.library.transfer.energy

import dev.architectury.fluid.FluidStack
import net.minecraft.core.Direction

interface EnergyContainer {
    fun getStored(side: Direction?): Long
    fun getCapacity(side: Direction?): Long

    fun accept(side: Direction?, energy: Long): Long // returns accepted energy
    fun output(side: Direction?, energy: Long): Long // returns output energy

    fun canAccept(side: Direction?): Boolean
    fun canOutput(side: Direction?): Boolean
}