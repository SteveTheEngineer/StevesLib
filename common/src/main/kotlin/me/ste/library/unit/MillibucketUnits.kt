package me.ste.library.unit

import dev.architectury.fluid.FluidStack
import me.ste.library.resource.FluidResource
import me.ste.library.resource.QuantifiedResource

object MillibucketUnits {
    val NUGGET = 16L
    val INGOT = 144L
    val BLOCK = 1296L

    val BOTTLE = 333L
    val BUCKET = 1000L

    val MAX_OPERATION_AMOUNT = Long.MAX_VALUE / 81L

    fun toPlatformUnits(millibuckets: Long): Long {
        if (millibuckets > MAX_OPERATION_AMOUNT) {
            return MAX_OPERATION_AMOUNT * (FluidStack.bucketAmount() / 1000L)
        }

        return millibuckets * (FluidStack.bucketAmount() / 1000L)
    }
    fun fromPlatformUnits(units: Long) = units / (FluidStack.bucketAmount() / 1000L)

    fun toPlatformUnits(resource: QuantifiedResource<FluidResource>) = QuantifiedResource(
        resource.resource, toPlatformUnits(resource.amount)
    )
    fun fromPlatformUnits(resource: QuantifiedResource<FluidResource>) = QuantifiedResource(
        resource.resource, fromPlatformUnits(resource.amount)
    )
}