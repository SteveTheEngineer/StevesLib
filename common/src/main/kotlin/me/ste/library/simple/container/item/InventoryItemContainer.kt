package me.ste.library.simple.container.item

import me.ste.library.simple.conversion.StackSnapshotItemContainer
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.item.ItemStack
import kotlin.math.min

open class InventoryItemContainer(
    size: Int,

    open val maxAccept: Int,
    open val maxOutput: Int,
    open val maxStackSize: Int = Container.LARGE_MAX_STACK_SIZE,

    protected val setChanged: Runnable
) : StackSnapshotItemContainer<List<ItemStack>> {
    protected open val stacks = NonNullList.withSize(size, ItemStack.EMPTY)

    val size get() = stacks.size

    fun setStack(slot: Int, stack: ItemStack) {
        if (slot < 0 || slot >= this.stacks.size) {
            return
        }

        if (stack.count > this.maxStackSize) {
            stack.count = this.maxStackSize
        }

        this.stacks[slot] = if (!stack.isEmpty) stack else ItemStack.EMPTY
        this.setChanged.run()
    }

    fun getStack(slot: Int): ItemStack {
        if (slot < 0 || slot >= this.stacks.size) {
            return ItemStack.EMPTY
        }

        return this.stacks[slot]
    }

    fun isCompatibleWith(slot: Int, item: StackableItem): Boolean {
        if (slot < 0 || slot >= this.stacks.size) {
            return false
        }

        val stack = this.stacks[slot]
        if (stack.isEmpty) {
            return true
        }

        return item.sameItemSameTag(stack)
    }

    fun getMaxStackSize(side: Direction?, slot: Int, stack: ItemStack): Int = min(stack.maxStackSize, this.getMaxStackSize(side, slot))

    open fun canOutput(side: Direction?, slot: Int, resource: StackableItem) = slot >= 0 && slot < this.stacks.size && this.canOutput(side)

    override fun getStack(side: Direction?, slot: Int): ItemStack {
        if (slot < 0 || slot >= this.stacks.size) {
            return ItemStack.EMPTY
        }

        return this.stacks[slot]
    }

    override fun getMaxStackSize(side: Direction?, slot: Int) = this.maxStackSize

    override fun canAccept(side: Direction?, slot: Int, resource: StackableItem) = slot >= 0 && slot < this.stacks.size && this.canAccept(side)

    override fun canAccept(side: Direction?) = this.maxAccept > 0

    override fun canOutput(side: Direction?) = this.maxOutput > 0

    override fun accept(side: Direction?, slot: Int, stack: ItemStack): Int {
        if (slot < 0 || slot >= this.stacks.size) {
            return 0
        }

        if (stack.isEmpty) {
            return 0
        }

        val item = StackableItem(stack)

        if (!this.canAccept(side, slot, item)) {
            return 0
        }

        val existingStack = this.stacks[slot]
        if (!existingStack.isEmpty && !ItemStack.isSameItemSameTags(existingStack, stack)) {
            return 0
        }

        val toAccept = stack.count.coerceAtMost(this.getMaxStackSize(side, slot, existingStack) - existingStack.count).coerceAtMost(this.maxAccept)
        if (toAccept <= 0) {
            return 0
        }

        val newStack = stack.copy()
        newStack.count = existingStack.count + toAccept
        this.stacks[slot] = newStack

        return toAccept
    }

    override fun output(side: Direction?, slot: Int, amount: Int): Int {
        if (slot < 0 || slot >= this.stacks.size) {
            return 0
        }

        val stack = this.stacks[slot]

        if (stack.isEmpty) {
            return 0
        }

        val item = StackableItem(stack)

        if (!this.canOutput(side, slot, item)) {
            return 0
        }

        val toOutput = amount.coerceAtMost(stack.count).coerceAtMost(this.maxOutput)

        val newStack = stack.copy()
        newStack.count -= toOutput

        this.stacks[slot] = if (!newStack.isEmpty) {
            newStack
        } else {
            ItemStack.EMPTY
        }

        return toOutput
    }

    override fun getContainerSize(side: Direction?) = this.stacks.size

    override fun createSnapshot(side: Direction?) = this.stacks.map { it.copy() }

    override fun saveChanges(side: Direction?) {
        this.setChanged.run()
    }

    override fun readSnapshot(side: Direction?, snapshot: List<ItemStack>) {
        for ((index, stack) in snapshot.withIndex()) {
            this.stacks[index] = stack
        }
    }

    fun save(tag: CompoundTag) {
        ContainerHelper.saveAllItems(tag, this.stacks)
    }

    fun load(tag: CompoundTag) {
        ContainerHelper.loadAllItems(tag, this.stacks)
    }
}