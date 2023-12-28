package me.ste.library.menu

import me.ste.library.util.MenuUtils
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class ExtendedMenu(
    menuType: MenuType<*>,
    containerId: Int,

    protected val inventory: Inventory
) : AbstractContainerMenu(menuType, containerId), MenuDataProvider {
    override val containerId get() = super.containerId

    protected val player get() = this.inventory.player
    protected val level get() = this.player.level()

    override fun broadcastChanges() {
        super.broadcastChanges()
        MenuData.sync(this, this.player as? ServerPlayer ?: return)
    }

    protected fun addPlayerInventorySlots(startX: Int = 8, startY: Int = 84) {
        MenuUtils.addPlayerInventorySlots(this::addSlot, this.inventory, startX, startY)
    }

    protected fun moveItemStackToExt(stack: ItemStack, range: IntRange, fromEnd: Boolean = false) =
        if (this.moveItemStackTo(stack, range.first, range.last + 1, fromEnd)) {
            stack
        } else {
            ItemStack.EMPTY
        }

    protected fun moveItemStackToExt(stack: ItemStack, slot: Int, fromEnd: Boolean = false) =
        this.moveItemStackToExt(stack, slot..slot, fromEnd)

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = this.getSlot(index) ?: return ItemStack.EMPTY

        if (!slot.hasItem()) {
            return ItemStack.EMPTY
        }

        val stack = slot.item

        val movedStack = this.quickMoveStackExt(player, index, slot, stack)
        slot.setChanged()
        return movedStack
    }

    abstract fun quickMoveStackExt(player: Player, index: Int, slot: Slot, stack: ItemStack): ItemStack
}