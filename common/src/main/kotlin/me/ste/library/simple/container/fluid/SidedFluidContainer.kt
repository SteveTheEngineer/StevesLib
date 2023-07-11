package me.ste.library.simple.container.fluid

import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.util.SnapshotUtils
import net.minecraft.core.Direction

open class SidedFluidContainer(
    protected val sides: Map<Direction?, SnapshotFluidContainer<*>>
) : SnapshotFluidContainer<Any?> {
    constructor(
        north: SnapshotFluidContainer<*>? = null,
        east: SnapshotFluidContainer<*>? = null,
        south: SnapshotFluidContainer<*>? = null,
        west: SnapshotFluidContainer<*>? = null,
        up: SnapshotFluidContainer<*>? = null,
        down: SnapshotFluidContainer<*>? = null,
        nullSide: SnapshotFluidContainer<*>? = null
    ) : this(
        mapOf(
            Direction.NORTH to north,
            Direction.EAST to east,
            Direction.SOUTH to south,
            Direction.WEST to west,
            Direction.UP to up,
            Direction.DOWN to down,
            null to nullSide
        ).filterValues { it != null } as Map<Direction?, SnapshotFluidContainer<*>>
    )

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.sides[side]?.accept(side, resource) ?: 0L

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.sides[side]?.output(side, resource) ?: 0L

    override fun getContainerSize(side: Direction?) = this.sides[side]?.getContainerSize(side) ?: 0

    override fun getResource(side: Direction?, slot: Int) = this.sides[side]?.getResource(side, slot) ?: ResourceWithAmount.EMPTY_FLUID

    override fun getCapacity(side: Direction?, slot: Int) = this.sides[side]?.getCapacity(side, slot) ?: 0L

    override fun canAccept(side: Direction?, slot: Int, resource: StackableFluid) = this.sides[side]?.canAccept(side, slot, resource) == true

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