package me.ste.library.util

import me.ste.library.container.EnergyContainer
import me.ste.library.container.PlatformContainers
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.lookup.block.BlockLookup
import me.ste.library.transaction.TransactionShard
import net.minecraft.core.Direction

object ContainerUtils {
    fun <T> transfer(sourceContainer: ResourceContainer<T>, resource: T, target: ResourceContainer<T>, maxAmount: Long, transaction: TransactionShard): Long {
        if (maxAmount <= 0L) {
            return 0L
        }

        val canOutput = transaction.openTransaction<Long> {
            sourceContainer.output(resource, maxAmount, it)
        }

        if (canOutput <= 0L) {
            return 0L
        }

        return transaction.openTransaction<Long> {
            val accepted = target.accept(resource, canOutput, it)
            val output = sourceContainer.output(resource, accepted, it)

            if (accepted != output) {
                return@openTransaction 0L
            }

            it.keep()
            accepted
        }
    }

    fun <T> transferAll(source: ResourceContainer<T>, target: ResourceContainer<T>, maxAmount: Long, transaction: TransactionShard): Long {
        var remaining = maxAmount

        for (holder in source) {
            if (remaining <= 0L) {
                break
            }

            if (holder.isEmpty) {
                continue
            }

            remaining -= this.transfer(source, holder.resource, target, remaining, transaction)
        }

        return maxAmount - remaining
    }

    fun transfer(source: EnergyContainer, target: EnergyContainer, maxAmount: Long, transaction: TransactionShard): Long {
        if (maxAmount <= 0L) {
            return 0L
        }

        val canOutput = transaction.openTransaction<Long> {
            source.output(maxAmount, it)
        }

        if (canOutput <= 0L) {
            return 0L
        }

        return transaction.openTransaction<Long> {
            val accepted = target.accept(canOutput, it)
            val output = source.output(accepted, it)

            if (accepted != output) {
                return@openTransaction 0L
            }

            it.keep()
            accepted
        }
    }
}