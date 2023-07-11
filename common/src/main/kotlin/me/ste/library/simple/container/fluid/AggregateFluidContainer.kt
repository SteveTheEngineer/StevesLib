package me.ste.library.simple.container.fluid

import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.util.SnapshotUtils
import net.minecraft.core.Direction
import java.util.function.BiFunction

open class AggregateFluidContainer(
    protected vararg val containers: SnapshotFluidContainer<*>
) : SnapshotFluidContainer<List<Any?>> {
    protected open fun <R> useSlot(side: Direction?, slot: Int, defaultValue: R, function: BiFunction<SnapshotFluidContainer<*>, Int, R>): R {
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

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        var remaining = resource

        for (container in this.containers) {
            if (remaining.amount <= 0) {
                return resource.amount
            }

            val accepted = container.accept(side, remaining)
            remaining = remaining.copy(amount = remaining.amount - accepted)
        }

        return resource.amount - remaining.amount
    }

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        var remaining = resource

        for (container in this.containers) {
            if (remaining.amount <= 0) {
                return resource.amount
            }

            val output = container.output(side, remaining)
            remaining = remaining.copy(amount = remaining.amount - output)
        }

        return resource.amount - remaining.amount
    }

    override fun getContainerSize(side: Direction?) =
        this.containers.fold(0) { acc, container -> acc + container.getContainerSize(side) }

    override fun getResource(side: Direction?, slot: Int) = this.useSlot(side, slot, ResourceWithAmount.EMPTY_FLUID) { container, containerSlot ->
        container.getResource(side, containerSlot)
    }

    override fun getCapacity(side: Direction?, slot: Int) = this.useSlot(side, slot, 0L) { container, containerSlot ->
        container.getCapacity(side, containerSlot)
    }

    override fun canAccept(side: Direction?, slot: Int, resource: StackableFluid) = this.useSlot(side, slot, false) { container, containerSlot ->
        container.canAccept(side, containerSlot, resource)
    }

    override fun canAccept(side: Direction?) = this.containers.any { it.canAccept(side) }

    override fun canOutput(side: Direction?) = this.containers.any { it.canOutput(side) }

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