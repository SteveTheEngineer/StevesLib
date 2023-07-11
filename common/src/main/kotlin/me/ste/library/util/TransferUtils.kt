package me.ste.library.util

import com.google.common.base.Predicate
import me.ste.library.simple.lookup.SimpleBlockLookup
import me.ste.library.transfer.energy.SimulatableEnergyContainer
import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.item.SimulatableItemContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import java.util.function.BiPredicate
import java.util.function.Consumer

object TransferUtils {
    fun output(container: SimulatableItemContainer, side: Direction?, resource: ResourceWithAmount<StackableItem>, simulate: Boolean): Long {
        var remaining = resource.amount

        for (slot in 0 until container.getContainerSize(side)) {
            val containedItem = container.getResource(side, slot)

            if (containedItem.resource.isEmpty) {
                continue
            }

            if (containedItem.resource != resource.resource) {
                continue
            }

            remaining -= if (simulate) {
                container.simulateOutput(side, slot, remaining)
            } else {
                container.output(side, slot, remaining)
            }
        }

        return resource.amount - remaining
    }

    fun insert(container: SimulatableItemContainer, side: Direction?, resource: ResourceWithAmount<StackableItem>, simulate: Boolean): Long {
        var remaining = resource.amount

        for (slot in 0 until container.getContainerSize(side)) {
            remaining -= if (simulate) {
                container.simulateAccept(side, slot, resource.copy(amount = remaining))
            } else {
                container.accept(side, slot, resource.copy(amount = remaining))
            }
        }

        return resource.amount - remaining
    }

    fun transferSingle(source: SimulatableItemContainer, sourceSide: Direction?, target: SimulatableItemContainer, targetSide: Direction?, resource: ResourceWithAmount<StackableItem>): Long {
        if (resource.resource.isEmpty) {
            return 0L
        }

        val availableAmount = output(source, sourceSide, resource, true)

        if (availableAmount <= 0L) {
            return 0L
        }

        val pushed = insert(target, targetSide, resource, false)

        if (pushed <= 0L) {
            return 0L
        }

        output(source, sourceSide, resource.copy(amount = pushed), false)
        return pushed
    }

    fun transferAll(source: SimulatableItemContainer, sourceSide: Direction?, target: SimulatableItemContainer, targetSide: Direction?, maxTransfer: Long): Long {
        var transfered = 0L

        for (slot in 0 until source.getContainerSize(sourceSide)) {
            if (transfered >= maxTransfer) {
                return transfered
            }

            val containedItem = source.getResource(sourceSide, slot).capAmount(maxTransfer - transfered)

            if (containedItem.resource.isEmpty) {
                continue
            }

            val availableAmount = source.simulateOutput(sourceSide, slot, containedItem.amount)

            if (availableAmount <= 0L) {
                continue
            }

            val pushed = insert(target, targetSide, containedItem, false)

            if (pushed <= 0L) {
                continue
            }

            source.output(sourceSide, slot, pushed)
            transfered += pushed
        }

        return transfered
    }

    fun transferSingle(source: SimulatableFluidContainer, sourceSide: Direction?, target: SimulatableFluidContainer, targetSide: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        if (resource.resource.isEmpty) {
            return 0L
        }

        val availableAmount = source.simulateOutput(sourceSide, resource)

        if (availableAmount <= 0L) {
            return 0L
        }

        val pushed = target.accept(targetSide, resource.copy(amount = availableAmount))

        if (pushed <= 0L) {
            return 0L
        }

        source.output(sourceSide, resource.copy(amount = pushed))
        return pushed
    }

    fun transferAll(source: SimulatableFluidContainer, sourceSide: Direction?, target: SimulatableFluidContainer, targetSide: Direction?, maxTransfer: Long): Long {
        var transfered = 0L

        for (slot in 0 until source.getContainerSize(sourceSide)) {
            if (transfered >= maxTransfer) {
                return transfered
            }

            val containedFluid = source.getResource(sourceSide, slot).capAmount(maxTransfer - transfered)

            if (containedFluid.resource.isEmpty) {
                continue
            }

            val availableAmount = source.simulateOutput(sourceSide, containedFluid)

            if (availableAmount <= 0L) {
                continue
            }

            val pushed = target.accept(targetSide, containedFluid)

            if (pushed <= 0L) {
                continue
            }

            source.output(sourceSide, containedFluid.copy(amount = pushed))
            transfered += pushed
        }

        return transfered
    }

    fun transferEnergy(source: SimulatableEnergyContainer, sourceSide: Direction?, target: SimulatableEnergyContainer, targetSide: Direction?, maxTransfer: Long): Long {
        if (maxTransfer <= 0L) {
            return 0L
        }

        val availableAmount = source.simulateOutput(sourceSide, maxTransfer)

        if (availableAmount <= 0L) {
            return 0L
        }

        val pushed = target.accept(targetSide, availableAmount)

        if (pushed <= 0L) {
            return 0L
        }

        source.output(sourceSide, pushed)
        return pushed
    }

    fun <T> forSides(level: Level, pos: BlockPos, lookup: SimpleBlockLookup<T>, sidePredicate: Predicate<Direction>, containerPredicate: BiPredicate<T, Direction>, callback: Consumer<T>) {
        for (side in Direction.values()) {
            if (!sidePredicate.test(side)) {
                continue
            }

            val container = lookup.get(level, pos.relative(side)) ?: continue

            if (!containerPredicate.test(container, side.opposite)) {
                continue
            }

            callback.accept(container)
        }
    }
}