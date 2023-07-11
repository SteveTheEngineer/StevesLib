package me.ste.library.simple.container.item

import me.ste.library.transfer.item.SnapshotItemContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableItem
import me.ste.library.util.SnapshotUtils
import net.minecraft.core.Direction
import java.util.function.BiFunction

open class AggregateItemContainer(
    protected vararg val containers: SnapshotItemContainer<*>
) : SnapshotItemContainer<List<Any?>> {
    protected open fun <R> useSlot(side: Direction?, slot: Int, defaultValue: R, function: BiFunction<SnapshotItemContainer<*>, Int, R>): R {
        if (slot < 0) {
            return defaultValue
        }

        var remaining = slot

        for (container in this.containers) {
            val size = container.getContainerSize(side)

            if (remaining < size) {
                return function.apply(container, remaining)
            }

            remaining -= size

            if (remaining < 0) {
                return defaultValue
            }
        }

        return defaultValue
    }

    override fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.useSlot(side, slot, 0) { container, containerSlot ->
        container.accept(side, containerSlot, resource)
    }

    override fun output(side: Direction?, slot: Int, amount: Long) = this.useSlot(side, slot, 0) { container, containerSlot ->
        container.output(side, containerSlot, amount)
    }

    override fun getContainerSize(side: Direction?) =
        this.containers.fold(0) { acc, container -> acc + container.getContainerSize(side) }

    override fun getResource(side: Direction?, slot: Int) = this.useSlot(side, slot, ResourceWithAmount.EMPTY_ITEM) { container, containerSlot ->
        container.getResource(side, containerSlot)
    }

    override fun getCapacity(side: Direction?, slot: Int) = this.useSlot(side, slot, 0L) { container, containerSlot ->
        container.getCapacity(side, containerSlot)
    }

    override fun canAccept(side: Direction?, slot: Int, resource: StackableItem) = this.useSlot(side, slot, false) { container, containerSlot ->
        container.canAccept(side, containerSlot, resource)
    }

    override fun canAccept(side: Direction?) = this.containers.any { it.canAccept(side) }

    override fun canOutput(side: Direction?) = this.containers.any { it.canAccept(side) }

    override fun createSnapshot(side: Direction?) = this.containers.map { it.createSnapshot(side) }

    override fun saveChanges(side: Direction?) {
        for (container in this.containers) {
            container.saveChanges(side)
        }
    }

    override fun readSnapshot(side: Direction?, snapshot: List<Any?>) {
        for ((index, containerSnapshot) in snapshot.withIndex()) {
            SnapshotUtils.readSnapshot(this.containers[index], side, containerSnapshot)
        }
    }

}