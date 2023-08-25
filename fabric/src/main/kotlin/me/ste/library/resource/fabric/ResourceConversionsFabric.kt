package me.ste.library.resource.fabric

import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import me.ste.library.resource.QuantifiedResource
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount

object ResourceConversionsFabric {
    fun toItemVariant(resource: ItemResource) = ItemVariant.of(resource.obj, resource.tag)
    fun toFluidVariant(resource: FluidResource) =  FluidVariant.of(resource.obj, resource.tag)

    fun toItemResourceAmount(resource: QuantifiedResource<ItemResource>) = ResourceAmount(toItemVariant(resource.resource), resource.amount)
    fun toFluidResourceAmount(resource: QuantifiedResource<FluidResource>) = ResourceAmount(toFluidVariant(resource.resource), resource.amount)

    fun fromItemVariant(variant: ItemVariant) = ItemResource(variant.item, variant.nbt)
    fun fromFluidVariant(variant: FluidVariant) = FluidResource(variant.fluid, variant.nbt)

    fun fromItemResourceAmount(resource: ResourceAmount<ItemVariant>) = QuantifiedResource(fromItemVariant(resource.resource), resource.amount)
    fun fromFluidResourceAmount(resource: ResourceAmount<FluidVariant>) = QuantifiedResource(fromFluidVariant(resource.resource), resource.amount)
}