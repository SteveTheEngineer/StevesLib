package me.ste.library.simple.slot.item

import dev.architectury.utils.Amount
import me.ste.library.container.SnapshotHolder
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.container.slotted.SlottedItemContainer
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.world.item.ItemStack

open class SlottedItemContainerHolder(
    protected val container: SlottedItemContainer,
    override val slot: Int
) : ResourceHolder<ItemResource> {
    private val snapshots = SnapshotHolder(this::stack, { this.container.setStack(this.slot, it) }) {}

    protected var stack: ItemStack
        get() = this.container.getStack(this.slot)
        set(value) { this.container.setStack(this.slot, value) }

    override val resource = ItemResource(this.stack)

    override val amount = this.stack.count.toLong()

    override val capacity = this.container.getMaxStackSize(this.slot).toLong()

    override val isEmpty = this.stack.isEmpty

    override fun accept(resource: ItemResource, amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        val currentStack = this.stack
        if (!currentStack.isEmpty && !resource.isSame(currentStack)) {
            return 0L
        }

        val acceptedStack = resource.toStack(Amount.toInt(amount))
        val max = acceptedStack.maxStackSize.coerceAtMost(this.container.getMaxStackSize(this.slot))

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

    override fun canOutput(resource: ItemResource) = this.container.canTake(this.slot, resource.toStack())

    override fun canAccept(resource: ItemResource) = this.container.canPlace(this.slot, resource.toStack())
}