package me.ste.library.resource

import dev.architectury.fluid.FluidStack
import dev.architectury.utils.Amount
import net.minecraft.world.item.ItemStack

data class QuantifiedResource<T : ResourceType>(
    val resource: T,
    val amount: Long
) {
    companion object {
        val EMPTY_ITEM = QuantifiedResource(ItemResource.EMPTY, 0L)
        val EMPTY_FLUID = QuantifiedResource(FluidResource.EMPTY, 0L)

        fun toFluidStack(resource: QuantifiedResource<FluidResource>) = resource.resource.toStack(resource.amount)
        fun toItemStack(resource: QuantifiedResource<ItemResource>) = resource.resource.toStack(Amount.toInt(resource.amount))

        fun fromFluidStack(stack: FluidStack) = QuantifiedResource(FluidResource(stack), stack.amount)
        fun fromItemStack(stack: ItemStack) = QuantifiedResource(ItemResource(stack), stack.count.toLong())
    }

    init {
        if (amount < 0L) {
            throw IllegalArgumentException("amount < 0")
        }
    }

    val isEmpty = this.amount == 0L || this.resource.isEmpty

    fun grow(amount: Long) = this.copy(amount = this.amount + amount)
    fun shrink(amount: Long) = this.copy(amount = (this.amount - amount).coerceAtLeast(0L))

    fun cap(amount: Long) = this.copy(amount = amount.coerceAtMost(amount))
    fun capToInt() = this.cap(Int.MAX_VALUE.toLong())
}
