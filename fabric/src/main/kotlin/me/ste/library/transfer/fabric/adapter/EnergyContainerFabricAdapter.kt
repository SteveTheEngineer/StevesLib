package me.ste.library.transfer.fabric.adapter

import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.energy.SnapshotEnergyContainer
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.core.Direction
import team.reborn.energy.api.EnergyStorage

open class EnergyContainerFabricAdapter(
    private val container: SnapshotEnergyContainer<*>,
    private val side: Direction?
) : SnapshotParticipant<Any?>(), EnergyStorage {
    override fun insert(energy: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)
        return this.container.accept(this.side, energy)
    }

    override fun extract(energy: Long, transaction: TransactionContext): Long {
        this.updateSnapshots(transaction)
        return this.container.output(this.side, energy)
    }

    override fun getAmount() = this.container.getStored(this.side)

    override fun getCapacity() = this.container.getCapacity(this.side)

    override fun createSnapshot() = this.container.createSnapshot(this.side)

    override fun readSnapshot(snapshot: Any?) {
        SnapshotUtils.readSnapshot(this.container, this.side, snapshot)
    }

    override fun onFinalCommit() {
        this.container.saveChanges(this.side)
    }
}