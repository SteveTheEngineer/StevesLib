package me.ste.library.transfer.forge.reverse

import me.ste.library.transfer.energy.SimulatableEnergyContainer
import net.minecraft.core.Direction
import net.minecraftforge.energy.IEnergyStorage
import java.util.function.Function

open class EnergyContainerReverseAdapter(
    private val handlers: Function<Direction?, IEnergyStorage?>
) : SimulatableEnergyContainer {
    private fun accept(side: Direction?, energy: Long, simulate: Boolean): Long {
        val handler = this.handlers.apply(side) ?: return 0L
        return handler.receiveEnergy(energy.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
    }

    private fun output(side: Direction?, energy: Long, simulate: Boolean): Long {
        val handler = this.handlers.apply(side) ?: return 0L
        return handler.extractEnergy(energy.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
    }

    override fun simulateAccept(side: Direction?, energy: Long) = this.accept(side, energy, true)

    override fun simulateOutput(side: Direction?, energy: Long) = this.output(side, energy, true)

    override fun accept(side: Direction?, energy: Long) = this.accept(side, energy, false)

    override fun output(side: Direction?, energy: Long) = this.output(side, energy, false)

    override fun getStored(side: Direction?) = this.handlers.apply(side)?.energyStored?.toLong() ?: 0L

    override fun getCapacity(side: Direction?) = this.handlers.apply(side)?.maxEnergyStored?.toLong() ?: 0L

    override fun canAccept(side: Direction?) = this.handlers.apply(side)?.canReceive() == true

    override fun canOutput(side: Direction?) = this.handlers.apply(side)?.canExtract() == true
}