package me.ste.library.simple.item

import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.ContainerHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack

open class SimpleWorldlyContainer(size: Int) : SimpleContainer(size), WorldlyContainer, Iterable<ItemStack> {
    override fun canPlaceItem(index: Int, stack: ItemStack) = this.canPlaceItemThroughFace(index, stack, null)

    override fun getSlotsForFace(side: Direction) = (0 until this.containerSize).toList().toIntArray()

    override fun canPlaceItemThroughFace(index: Int, stack: ItemStack, side: Direction?) = true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, side: Direction) = true

    fun save(tag: CompoundTag, saveEmpty: Boolean = true) {
        val list = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)

        for (i in 0 until this.containerSize) {
            list[i] = this.getItem(i)
        }

        ContainerHelper.saveAllItems(tag, list, saveEmpty)
    }

    fun load(tag: CompoundTag) {
        val list = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)

        ContainerHelper.loadAllItems(tag, list)

        for ((i, stack) in list.withIndex()) {
            this.setItem(i, stack)
        }
    }

    override fun iterator() = object : Iterator<ItemStack> {
        private var slot = 0

        override fun hasNext() = slot < this@SimpleWorldlyContainer.containerSize

        override fun next() = this@SimpleWorldlyContainer.getItem(this.slot++)
    }

    fun cut(slots: IntRange) = SimpleContainer(*this.toList().slice(slots).toTypedArray())
    fun cut(slot: Int) = this.cut(slot..slot)
}