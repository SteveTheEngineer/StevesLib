package me.ste.library.simple.slot.item

import me.ste.library.container.slotted.SlottedItemContainer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

open class SimpleSlottedItemContainer(size: Int, private val onChange: Runnable) : SlottedItemContainer {
    protected val stacks = Array(size) { ItemStack.EMPTY }

    override val size get() = this.stacks.size

    override val isEmpty = this.stacks.all { it.isEmpty }

    override fun getStack(slot: Int) = this.stacks.getOrElse(slot) { ItemStack.EMPTY }.copy()

    override fun setStack(slot: Int, stack: ItemStack) {
        if (slot < 0 || slot >= this.stacks.size) {
            return
        }

        this.stacks[slot] = stack.copy()
        this.onChange.run()
    }

    override fun canPlace(slot: Int, stack: ItemStack) = true

    override fun canTake(slot: Int, stack: ItemStack) = true

    override fun getMaxStackSize(slot: Int) = Container.LARGE_MAX_STACK_SIZE

    fun save(): ListTag {
        val tag = ListTag()

        for ((slot, stack) in this.stacks.withIndex()) {
            if (stack.isEmpty) {
                continue
            }

            val stackTag = CompoundTag()
            stack.save(stackTag)
            stackTag.putByte("Slot", slot.toByte())

            tag.add(stackTag)
        }

        return tag
    }

    fun load(tag: ListTag) {
        this.stacks.fill(ItemStack.EMPTY)

        for (element in tag) {
            val stackTag = element as? CompoundTag
                ?: continue

            val stack = ItemStack.of(stackTag)
            val slot = stackTag.getByte("Slot")

            if (slot < 0 || slot >= this.stacks.size) {
                continue
            }

            this.stacks[slot.toInt()] = stack
        }
    }
}