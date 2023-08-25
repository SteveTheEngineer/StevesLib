package me.ste.library.container.forge.reverse

import me.ste.library.container.EnergyContainer
import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.absoluteValue

open class EnergyContainerReverseAdapter(
    private val storage: IEnergyStorage
) : EnergyContainer {
    private val snapshots = mutableMapOf<Int, Int>()
    private var change = 0

    private fun doChange(simulate: Boolean) =
        if (this.change > 0) {
            this.storage.receiveEnergy(this.change, simulate)
        } else if (this.change < 0) {
            -this.storage.extractEnergy(-this.change, simulate)
        } else {
            0
        }

    private fun exchange(energy: Int, transaction: TransactionShard): Int {
        this.track(transaction)

        val oldChange = this.change
        this.change += energy
        val newChange = this.doChange(true)
        this.change = newChange

        return (newChange - oldChange).absoluteValue
    }

    private fun track(transaction: TransactionShard) {
        if (transaction.depth in this.snapshots) {
            return
        }

        transaction.onEnd {
            if (it == TransactionResult.REVERT) {
                this.change = this.snapshots[transaction.depth]!!
            }

            this.snapshots -= transaction.depth
        }

        if (this.snapshots.isEmpty()) {
            transaction.onFinalEnd {
                if (it != TransactionResult.KEEP) {
                    this.change = 0
                    return@onFinalEnd
                }

                this.doChange(false)
                this.change = 0
            }
        }

        this.snapshots[transaction.depth] = this.change
    }

    override val stored get() = (this.storage.energyStored + this.change).toLong()

    override val capacity get() = this.storage.maxEnergyStored.toLong()

    override fun accept(energy: Long, transaction: TransactionShard): Long {
        return this.exchange(energy.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt(), transaction).toLong()
    }

    override fun output(energy: Long, transaction: TransactionShard): Long {
        return this.exchange(-(energy.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()), transaction).toLong()
    }

    override val canAccept get() = this.storage.canReceive()

    override val canOutput get() = this.storage.canExtract()
}