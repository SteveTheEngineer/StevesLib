package me.ste.library.container.forge

import me.ste.library.container.EnergyContainer
import me.ste.library.transaction.Transactions
import net.minecraftforge.energy.IEnergyStorage

class EnergyContainerAdapter(
    val container: EnergyContainer
) : IEnergyStorage {
    override fun receiveEnergy(energy: Int, simulate: Boolean): Int {
        var accepted = 0

        Transactions.open {
            accepted = this.container.accept(energy.toLong(), it).toInt()

            if (!simulate) {
                it.keep()
            }
        }

        return accepted
    }

    override fun extractEnergy(energy: Int, simulate: Boolean): Int {
        var output = 0

        Transactions.open {
            output = this.container.output(energy.toLong(), it).toInt()

            if (!simulate) {
                it.keep()
            }
        }

        return output
    }

    override fun getEnergyStored() = this.container.stored.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    override fun getMaxEnergyStored() = this.container.capacity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    override fun canExtract() = this.container.canOutput

    override fun canReceive() = this.container.canAccept
}