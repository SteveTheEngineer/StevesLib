package me.ste.library.transfer.fabric.reverse

import me.ste.library.transfer.energy.SimulatableEnergyContainer
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.Direction
import team.reborn.energy.api.EnergyStorage
import java.util.function.Function

open class EnergyContainerReverseAdapter(
    private val storages: Function<Direction?, EnergyStorage?>
) : SimulatableEnergyContainer {
    private fun accept(side: Direction?, energy: Long, simulate: Boolean): Long {
        val storage = this.storages.apply(side) ?: return 0L

        return Transaction.openOuter().use {
            val amount = storage.insert(energy, it)

            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }

            amount
        }
    }

    private fun output(side: Direction?, energy: Long, simulate: Boolean): Long {
        val storage = this.storages.apply(side) ?: return 0L

        return Transaction.openOuter().use {
            val amount = storage.extract(energy, it)

            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }

            amount
        }
    }

    override fun simulateAccept(side: Direction?, energy: Long) = this.accept(side, energy, true)

    override fun simulateOutput(side: Direction?, energy: Long) = this.output(side, energy, true)

    override fun accept(side: Direction?, energy: Long) = this.accept(side, energy, false)

    override fun output(side: Direction?, energy: Long) = this.accept(side, energy, false)

    override fun getStored(side: Direction?) = this.storages.apply(side)?.amount ?: 0L

    override fun getCapacity(side: Direction?) = this.storages.apply(side)?.capacity ?: 0L

    override fun canAccept(side: Direction?) = this.storages.apply(side)?.supportsInsertion() == true

    override fun canOutput(side: Direction?) = this.storages.apply(side)?.supportsExtraction() == true
}