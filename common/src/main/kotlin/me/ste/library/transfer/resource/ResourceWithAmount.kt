package me.ste.library.transfer.resource

import dev.architectury.fluid.FluidStack
import net.minecraft.world.item.ItemStack
import kotlin.math.min

data class ResourceWithAmount<T>(
    val resource: T,
    val amount: Long
) {
    companion object {
        val EMPTY_ITEM = empty(StackableItem.EMPTY)
        val EMPTY_FLUID = empty(StackableFluid.EMPTY)

        fun from(stack: ItemStack) = ResourceWithAmount(
            StackableItem(stack), stack.count.toLong()
        )

        fun from(stack: FluidStack) = ResourceWithAmount(
            StackableFluid(stack), stack.amount
        )

        fun <T> empty(emptyResource: T) = ResourceWithAmount(emptyResource, 0L)

        fun fromItemStack(stack: ItemStack) = ResourceWithAmount(
            StackableItem(stack), stack.count.toLong()
        )

        fun toItemStack(resource: ResourceWithAmount<StackableItem>): ItemStack {
            if (resource.amount > Int.MAX_VALUE) {
                throw IllegalArgumentException("Resource amount too large! ${resource.amount} > ${Int.MAX_VALUE}")
            }

            return resource.resource.toStack(resource.amount.toInt())
        }

        fun fromFluidStack(stack: FluidStack) = ResourceWithAmount(
            StackableFluid(stack), stack.amount
        )

        fun toFluidStack(resource: ResourceWithAmount<StackableFluid>) =
            resource.resource.toStack(resource.amount)
    }

    fun capAmount(max: Long) = this.copy(amount = min(this.amount, max))
    fun capToInt() = this.capAmount(Int.MAX_VALUE.toLong())

    fun grow(amount: Long) = this.copy(amount = this.amount + amount)
    fun shrink(amount: Long) = this.grow(-amount)
}