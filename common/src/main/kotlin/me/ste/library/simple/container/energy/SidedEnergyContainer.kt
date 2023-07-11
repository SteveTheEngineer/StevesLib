package me.ste.library.simple.container.energy

import me.ste.library.transfer.energy.SnapshotEnergyContainer
import me.ste.library.transfer.item.SnapshotItemContainer
import me.ste.library.util.SnapshotUtils
import net.minecraft.core.Direction

open class SidedEnergyContainer(
    protected val sides: Map<Direction?, SnapshotEnergyContainer<*>>
) : SnapshotEnergyContainer<Any?> {
    constructor(
        north: SnapshotEnergyContainer<*>? = null,
        east: SnapshotEnergyContainer<*>? = null,
        south: SnapshotEnergyContainer<*>? = null,
        west: SnapshotEnergyContainer<*>? = null,
        up: SnapshotEnergyContainer<*>? = null,
        down: SnapshotEnergyContainer<*>? = null,
        nullSide: SnapshotEnergyContainer<*>? = null
    ) : this(
        mapOf(
            Direction.NORTH to north,
            Direction.EAST to east,
            Direction.SOUTH to south,
            Direction.WEST to west,
            Direction.UP to up,
            Direction.DOWN to down,
            null to nullSide
        ).filterValues { it != null } as Map<Direction?, SnapshotEnergyContainer<*>>
    )

    override fun getStored(side: Direction?) = this.sides[side]?.getStored(side) ?: 0L

    override fun getCapacity(side: Direction?) = this.sides[side]?.getCapacity(side) ?: 0L

    override fun accept(side: Direction?, energy: Long) = this.sides[side]?.accept(side, energy) ?: 0L

    override fun output(side: Direction?, energy: Long) = this.sides[side]?.output(side, energy) ?: 0L

    override fun canAccept(side: Direction?) = this.sides[side]?.canAccept(side) == true

    override fun canOutput(side: Direction?) = this.sides[side]?.canOutput(side) == true

    override fun createSnapshot(side: Direction?) = this.sides[side]?.createSnapshot(side)

    override fun saveChanges(side: Direction?) {
        this.sides[side]?.saveChanges(side)
    }

    override fun readSnapshot(side: Direction?, snapshot: Any?) {
        val container = this.sides[side] ?: return
        SnapshotUtils.readSnapshot(container, side, snapshot)
    }
}