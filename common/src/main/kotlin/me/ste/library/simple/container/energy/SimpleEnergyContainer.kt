package me.ste.library.simple.container.energy

import me.ste.library.transfer.energy.SnapshotEnergyContainer
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag

open class SimpleEnergyContainer(
    open val capacity: Long,

    open val maxAccept: Long,
    open val maxOutput: Long,

    protected val setChanged: Runnable
) : SnapshotEnergyContainer<Long> {
    protected var stored = 0L

    override fun getStored(side: Direction?) = this.stored

    override fun getCapacity(side: Direction?) = this.capacity

    override fun accept(side: Direction?, energy: Long): Long {
        val toAccept = energy.coerceAtMost(this.capacity - this.stored).coerceAtMost(this.maxAccept)
        this.stored += toAccept
        return toAccept
    }

    override fun output(side: Direction?, energy: Long): Long {
        val toOutput = energy.coerceAtMost(this.stored).coerceAtMost(this.maxOutput)
        this.stored -= toOutput
        return toOutput
    }

    override fun canAccept(side: Direction?) = this.maxAccept > 0L

    override fun canOutput(side: Direction?) = this.maxOutput > 0L

    override fun createSnapshot(side: Direction?) = this.stored

    override fun saveChanges(side: Direction?) {
        this.setChanged.run()
    }

    override fun readSnapshot(side: Direction?, snapshot: Long) {
        this.stored = snapshot
    }

    fun save(tag: CompoundTag) {
        tag.putLong("Energy", this.stored)
    }

    fun load(tag: CompoundTag) {
        this.stored = tag.getLong("Energy")
    }
}