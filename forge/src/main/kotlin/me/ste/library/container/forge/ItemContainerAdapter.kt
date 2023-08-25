package me.ste.library.container.forge

import me.ste.library.container.resource.ResourceContainer
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.Transactions
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler

class ItemContainerAdapter(
     val container: ResourceContainer<ItemResource>
) : IItemHandler {
    private val usesSlots get() = this.container.slots != -1

    override fun getSlots(): Int {
        val slots = this.container.slots

        if (slots == -1) {
            return this.container.count()
        }

        return slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        val holder = if (this.usesSlots) {
            this.container.getSlot(slot)
        } else {
            this.container.elementAtOrNull(slot)
        } ?: return ItemStack.EMPTY

        return holder.resource.toStack(
            holder.amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        )
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val resource = ItemResource(stack)

        if (this.usesSlots) {
            val holder = this.container.getSlot(slot)
            if (holder != null && holder.canAccept && holder.canAccept(resource)) {
                var accepted = 0

                Transactions.open {
                    accepted = holder.accept(resource, stack.count.toLong(), it).toInt()

                    if (!simulate) {
                        it.keep()
                    }
                }

                return resource.toStack(stack.count - accepted)
            }

            return stack
        }

        val holder = this.container.elementAtOrNull(slot)
        if (holder != null && holder.canAccept && holder.canAccept(resource)) {
            var accepted = 0

            Transactions.open {
                accepted = holder.accept(resource, stack.count.toLong(), it).toInt()

                if (!simulate) {
                    it.keep()
                }
            }

            return resource.toStack(stack.count - accepted)
        }

        var accepted = 0

        Transactions.open {
            accepted = this.container.accept(resource, stack.count.toLong(), it).toInt()

            if (!simulate) {
                it.keep()
            }
        }

        return resource.toStack(stack.count - accepted)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (this.usesSlots) {
            val holder = this.container.getSlot(slot)
            if (holder != null && holder.canAccept && holder.canOutput(holder.resource)) {
                var output = 0

                Transactions.open {
                    output = holder.output(amount.toLong(), it).toInt()

                    if (!simulate) {
                        it.keep()
                    }
                }

                return holder.resource.toStack(output)
            }

            return ItemStack.EMPTY
        }

        val holder = this.container.elementAtOrNull(slot)
        if (holder != null && holder.canAccept && holder.canOutput(holder.resource)) {
            var output = 0

            Transactions.open {
                output = holder.output(amount.toLong(), it).toInt()

                if (!simulate) {
                    it.keep()
                }
            }

            return holder.resource.toStack(output)
        }

        return ItemStack.EMPTY
    }

    override fun getSlotLimit(slot: Int): Int {
        val holder = if (this.usesSlots) {
            this.container.getSlot(slot)
        } else {
            this.container.elementAtOrNull(slot)
        } ?: return 0

        return holder.capacity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        val resource = ItemResource(stack)

        val holder = if (this.usesSlots) {
            this.container.getSlot(slot)
        } else {
            this.container.elementAtOrNull(slot)
        } ?: return this.container.canAccept(
            resource
        )

        return holder.canAccept(resource)
    }
}