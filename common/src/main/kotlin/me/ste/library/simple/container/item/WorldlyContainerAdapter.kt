package me.ste.library.simple.container.item

import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

open class WorldlyContainerAdapter(
    protected open val container: InventoryItemContainer,
    protected open val stillValid: Predicate<Player> = Predicate { false }
) : WorldlyContainer {
    override fun clearContent() {
        for (slot in 0 until this.container.size) {
            this.container.setStack(slot, ItemStack.EMPTY)
        }
    }

    override fun getContainerSize() = this.container.size

    override fun isEmpty(): Boolean {
        for (slot in 0 until this.container.size) {
            if (!this.container.getStack(slot).isEmpty) {
                return false
            }
        }

        return true
    }

    override fun getItem(slot: Int) = this.container.getStack(slot)

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val stack = this.container.getStack(slot)
        val toRemove = amount.coerceAtMost(stack.count)

        val removed = stack.copy()
        removed.count = toRemove

        stack.shrink(toRemove)
        this.container.setStack(slot, stack)

        return if (!removed.isEmpty) removed else ItemStack.EMPTY
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val stack = this.container.getStack(slot)
        this.container.setStack(slot, ItemStack.EMPTY)
        return stack
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        this.container.setStack(slot, stack)
    }

    override fun setChanged() {}

    override fun stillValid(player: Player) = this.stillValid.test(player)

    override fun getSlotsForFace(side: Direction) = (0 until this.container.size).toList().toIntArray()

    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, side: Direction?) =
        this.container.canAccept(side, slot, StackableItem(stack))

    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, side: Direction) =
        this.container.canOutput(side, slot, StackableItem(stack))

    override fun canPlaceItem(slot: Int, stack: ItemStack) =
        this.container.canAccept(null, slot, StackableItem(stack))
}