package me.ste.library.transfer.forge.adapter

import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.energy.SnapshotEnergyContainer
import net.minecraft.core.Direction
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.min

open class EnergyContainerForgeAdapter(
    private val container: SnapshotEnergyContainer<*>,
    private val side: Direction?
) : IEnergyStorage {
    override fun receiveEnergy(energy: Int, simulate: Boolean): Int {
        val accepted = SnapshotUtils.simulate(this.container, this.side, simulate) {
            it.accept(this.side, energy.toLong())
        }

        if (accepted > energy) {
            throw IllegalStateException("Accepted energy is greater than pushed.")
        }

        return accepted.toInt()
    }

    override fun extractEnergy(energy: Int, simulate: Boolean): Int {
        val output = SnapshotUtils.simulate(this.container, this.side, simulate) {
            it.output(this.side, energy.toLong())
        }

        if (output > energy) {
            throw IllegalStateException("Output energy is greater than pushed.")
        }

        return output.toInt()
    }

    override fun getEnergyStored() = min(
        this.container.getStored(this.side),
        Int.MAX_VALUE.toLong()
    ).toInt()

    override fun getMaxEnergyStored() = min(
        this.container.getCapacity(this.side),
        Int.MAX_VALUE.toLong()
    ).toInt()

    override fun canExtract() = this.container.canOutput(this.side)

    override fun canReceive() = this.container.canAccept(this.side)
}