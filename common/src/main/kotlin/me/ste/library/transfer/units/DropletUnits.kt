package me.ste.library.transfer.units

import dev.architectury.fluid.FluidStack
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid

object DropletUnits {
    val NUGGET = 1000L
    val INGOT = 9000L
    val BOTTLE = 27000L
    val BLOCK = 81000L
    val BUCKET = 81000L

    val MAX_OPERATION_AMOUNT = Long.MAX_VALUE

    fun toPlatformUnits(droplets: Long) = droplets / (81000L / FluidStack.bucketAmount())
    fun fromPlatformUnits(units: Long) = units * (81000L / FluidStack.bucketAmount())

    fun toPlatformUnits(resource: ResourceWithAmount<StackableFluid>) = ResourceWithAmount(
        resource.resource, toPlatformUnits(resource.amount)
    )

    fun fromPlatformUnits(resource: ResourceWithAmount<StackableFluid>) = ResourceWithAmount(
        resource.resource, fromPlatformUnits(resource.amount)
    )
}