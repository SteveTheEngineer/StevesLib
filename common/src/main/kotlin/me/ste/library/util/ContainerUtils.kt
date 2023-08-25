package me.ste.library.util

import me.ste.library.container.EnergyContainer
import me.ste.library.container.PlatformContainers
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.lookup.block.BlockLookup
import me.ste.library.transaction.TransactionShard
import net.minecraft.core.Direction

object ContainerUtils {
    fun <T> transfer(source: ResourceHolder<T>, target: ResourceContainer<T>, maxAmount: Long, transaction: TransactionShard): Long {
        if (maxAmount <= 0L || source.isEmpty) {
            return 0L
        }

        val resource = source.resource

        val canOutput = transaction.openTransaction<Long> {
            source.output(maxAmount, it)
        }

        return transaction.openTransaction<Long> {
            val accepted = target.accept(resource, canOutput, it)
            val output = source.output(accepted, it)

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

            remaining -= this.transfer(holder, target, remaining, transaction)
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