package me.ste.library.simple.slot.fluid

import dev.architectury.fluid.FluidStack
import me.ste.library.container.slotted.SlottedFluidContainer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack

open class SimpleSlottedFluidContainer(size: Int, protected val capacity: Long) : SlottedFluidContainer {
    protected val fluids = Array(size) { FluidStack.empty() }

    override val size get() = this.fluids.size

    override val isEmpty = this.fluids.all { it.isEmpty }

    override fun getFluid(slot: Int) = this.fluids.getOrElse(slot) { FluidStack.empty() }.copy()

    override fun setFluid(slot: Int, stack: FluidStack) {
        if (slot < 0 || slot >= this.fluids.size) {
            return
        }

        this.fluids[slot] = stack.copy()
    }

    override fun canPlace(slot: Int, stack: FluidStack) = true

    override fun canTake(slot: Int, stack: FluidStack) = true

    override fun getCapacity(slot: Int) = this.capacity

    fun save(): ListTag {
        val tag = ListTag()

        for ((slot, stack) in this.fluids.withIndex()) {
            if (stack.isEmpty) {
                continue
            }

            val fluidTag = CompoundTag()
            stack.write(fluidTag)
            fluidTag.putByte("Slot", slot.toByte())

            tag.add(fluidTag)
        }

        return tag
    }

    fun load(tag: ListTag) {
        this.fluids.fill(FluidStack.empty())

        for (element in tag) {
            val fluidTag = element as? CompoundTag
                ?: continue

            val fluid = FluidStack.read(fluidTag)
            val slot = fluidTag.getByte("Slot")

            if (slot < 0 || slot >= this.fluids.size) {
                continue
            }

            this.fluids[slot.toInt()] = fluid
        }
    }
}