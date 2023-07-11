package me.ste.library.transfer.units

import dev.architectury.fluid.FluidStack
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid

object MillibucketUnits {
    val NUGGET = 16L
    val INGOT = 144L
    val BLOCK = 1296L

    val BOTTLE = 333L
    val BUCKET = 1000L

    val MAX_OPERATION_AMOUNT = Long.MAX_VALUE / 81L

    fun toPlatformUnits(millibuckets: Long): Long {
        if (millibuckets > MAX_OPERATION_AMOUNT) {
            throw IllegalArgumentException("Provided value is greater than the maximum operation amount. $millibuckets > $MAX_OPERATION_AMOUNT")
        }

        return millibuckets * (FluidStack.bucketAmount() / 1000L)
    }
    fun fromPlatformUnits(units: Long) = units / (FluidStack.bucketAmount() / 1000L)

    fun toPlatformUnits(resource: ResourceWithAmount<StackableFluid>) = ResourceWithAmount(
        resource.resource, toPlatformUnits(resource.amount)
    )
    fun fromPlatformUnits(resource: ResourceWithAmount<StackableFluid>) = ResourceWithAmount(
        resource.resource, fromPlatformUnits(resource.amount)
    )
}