package me.ste.library.simple

import me.ste.library.container.SnapshotHolder
import me.ste.library.container.resource.SingleSlotResourceContainer
import me.ste.library.resource.ItemResource
import me.ste.library.resource.QuantifiedResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class SingleSlotConsumer(
    stack: ItemStack,
    private val setStack: Consumer<ItemStack>
) : SingleSlotResourceContainer<ItemResource> {
    private val snapshots = SnapshotHolder(
            { QuantifiedResource(this.resource, this.amount) },
            {
                this.resource = it.resource
                this.amount = it.amount
            }
    ) {
        this.setStack.accept(this.resource.toStack(this.amount.toInt()))
    }

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        val toAccept = amount.coerceAtMost(this.capacity - this.amount)
        this.amount += toAccept

        if (this.amount > 0L) {
            this.resource = resource
        }

        return toAccept
    }


    override fun output(amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        val toOutput = amount.coerceAtMost(this.amount)
        this.amount -= toOutput

        if (this.amount == 0L) {
            this.resource = ItemResource.EMPTY
        }

        return toOutput
    }

    override fun canAccept(resource: ItemResource) = true
    override fun canOutput(resource: ItemResource) = true

    override val canAccept = true
    override val canOutput = true

    override var resource = ItemResource(stack)
    override var amount = stack.count.toLong()

    override val capacity get() = this.resource.toStack(this.amount.toInt()).maxStackSize.toLong()
    override val isEmpty = this.resource.isEmpty || this.amount <= 0
}