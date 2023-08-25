package me.ste.library.simple.slot.item

import me.ste.library.container.slotted.SlottedItemContainer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class SlottedItemContainerSlot(val itemContainer: SlottedItemContainer, slot: Int, x: Int, y: Int) : Slot(SimpleContainer(0), slot, x, y) {
    override fun mayPlace(stack: ItemStack) = this.itemContainer.canPlace(this.containerSlot, stack)

    override fun mayPickup(player: Player): Boolean {
        val stack = this.itemContainer.getStack(this.containerSlot)

        if (stack.isEmpty) {
            return true
        }

        return this.itemContainer.canTake(this.containerSlot, stack)
    }
    
    override fun getItem() = this.itemContainer.getStack(this.containerSlot)

    override fun set(stack: ItemStack) {
        this.itemContainer.setStack(this.containerSlot, stack)
        this.setChanged()
    }

    override fun initialize(stack: ItemStack) {
        this.itemContainer.setStack(this.containerSlot, stack)
        this.setChanged()
    }

    override fun remove(amount: Int): ItemStack {
        val stack = this.itemContainer.getStack(this.containerSlot)
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }

        val removed = stack.copy()
        removed.count = amount.coerceAtMost(stack.count)

        stack.count -= removed.count
        this.itemContainer.setStack(this.containerSlot, stack)

        return removed
    }

    override fun getMaxStackSize() = this.itemContainer.getMaxStackSize(this.containerSlot)

    override fun setChanged() {}
}