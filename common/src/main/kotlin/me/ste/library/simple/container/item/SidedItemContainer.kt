package me.ste.library.simple.container.item

import me.ste.library.transfer.item.SnapshotItemContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableItem
import me.ste.library.util.SnapshotUtils
import net.minecraft.core.Direction

open class SidedItemContainer(
    protected val sides: Map<Direction?, SnapshotItemContainer<*>>
) : SnapshotItemContainer<Any?> {
    constructor(
        north: SnapshotItemContainer<*>? = null,
        east: SnapshotItemContainer<*>? = null,
        south: SnapshotItemContainer<*>? = null,
        west: SnapshotItemContainer<*>? = null,
        up: SnapshotItemContainer<*>? = null,
        down: SnapshotItemContainer<*>? = null,
        nullSide: SnapshotItemContainer<*>? = null
    ) : this(
        mapOf(
            Direction.NORTH to north,
            Direction.EAST to east,
            Direction.SOUTH to south,
            Direction.WEST to west,
            Direction.UP to up,
            Direction.DOWN to down,
            null to nullSide
        ).filterValues { it != null } as Map<Direction?, SnapshotItemContainer<*>>
    )

    override fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.sides[side]?.accept(side, slot, resource) ?: 0L

    override fun output(side: Direction?, slot: Int, amount: Long) = this.sides[side]?.output(side, slot, amount) ?: 0L

    override fun getContainerSize(side: Direction?) = this.sides[side]?.getContainerSize(side) ?: 0

    override fun getResource(side: Direction?, slot: Int) = this.sides[side]?.getResource(side, slot) ?: ResourceWithAmount.EMPTY_ITEM

    override fun getCapacity(side: Direction?, slot: Int) = this.sides[side]?.getCapacity(side, slot) ?: 0L

    override fun canAccept(side: Direction?, slot: Int, resource: StackableItem) = this.sides[side]?.canAccept(side, slot, resource) == true

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