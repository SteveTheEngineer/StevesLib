package me.ste.library.simple.item

import dev.architectury.utils.Amount
import me.ste.library.container.SnapshotHolder
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.core.Direction
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack

open class WorldlyContainerHolder(
    protected val container: WorldlyContainer,
    protected val side: Direction,

    override val slot: Int
) : ResourceHolder<ItemResource> {
    private val snapshots = SnapshotHolder({ this.stack.copy() }, { this.stack = it }) {
        this.container.setChanged()
    }

    protected var stack: ItemStack
        get() = this.container.getItem(this.slot)
        set(value) { this.container.setItem(this.slot, value) }

    override val resource = ItemResource(this.stack)

    override val amount = this.stack.count.toLong()

    override val capacity = this.container.maxStackSize.toLong()

    override val isEmpty = this.stack.isEmpty

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        if (!this.canAccept(resource)) {
            return 0L
        }

        val currentStack = this.stack
        if (!currentStack.isEmpty && !resource.isSame(currentStack)) {
            return 0L
        }

        val acceptedStack = resource.toStack(Amount.toInt(amount))
        val max = acceptedStack.maxStackSize.coerceAtMost(Amount.toInt(this.capacity))

        val toAccept = acceptedStack.count.coerceAtMost(max - currentStack.count)
        if (toAccept <= 0) {
            return 0L
        }

        acceptedStack.count = currentStack.count + toAccept
        this.stack = acceptedStack
        return toAccept.toLong()
    }

    override fun output(amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        if (!this.canOutput(this.resource)) {
            return 0L
        }

        val stack = this.stack
        if (stack.isEmpty) {
            return 0L
        }

        val toOutput = Amount.toInt(amount).coerceAtMost(stack.count)
        if (toOutput <= 0L) {
            return 0L
        }

        stack.count -= toOutput
        this.stack = if (!stack.isEmpty) stack else ItemStack.EMPTY

        return toOutput.toLong()
    }

    override val canAccept get() = true
    override val canOutput get() = true

    override fun canOutput(resource: ItemResource) = this.container.canTakeItemThroughFace(this.slot, resource.toStack(), this.side)

    override fun canAccept(resource: ItemResource): Boolean {
        val stack = resource.toStack()

        if (!this.container.canPlaceItem(this.slot, stack)) {
            return false
        }

        return this.container.canPlaceItemThroughFace(this.slot, stack, this.side)
    }
}