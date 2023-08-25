package me.ste.library.unit

import dev.architectury.fluid.FluidStack
import me.ste.library.resource.FluidResource
import me.ste.library.resource.QuantifiedResource

object DropletUnits {
    val NUGGET = 1000L
    val INGOT = 9000L
    val BOTTLE = 27000L
    val BLOCK = 81000L
    val BUCKET = 81000L

    val MAX_OPERATION_AMOUNT = Long.MAX_VALUE

    fun toPlatformUnits(droplets: Long) = droplets / (81000L / FluidStack.bucketAmount())
    fun fromPlatformUnits(units: Long) = units * (81000L / FluidStack.bucketAmount())

    fun toPlatformUnits(resource: QuantifiedResource<FluidResource>) = QuantifiedResource(
        resource.resource, toPlatformUnits(resource.amount)
    )

    fun fromPlatformUnits(resource: QuantifiedResource<FluidResource>) = QuantifiedResource(
        resource.resource, fromPlatformUnits(resource.amount)
    )
}