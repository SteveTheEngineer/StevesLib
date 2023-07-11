package me.ste.library.fabric

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import me.ste.library.transfer.resource.StackableItem
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount

object ResourceConversionsFabric {
    fun fromFabric(variant: ItemVariant): StackableItem {
        if (variant.isBlank) {
            return StackableItem.EMPTY
        }

        return StackableItem(variant.item, variant.nbt)
    }
    fun fromFabric(variant: FluidVariant): StackableFluid {
        if (variant.isBlank) {
            return StackableFluid.EMPTY
        }

        return StackableFluid(variant.fluid, variant.nbt)
    }

    fun fromFabricWithAmountItem(resource: ResourceAmount<ItemVariant>) = ResourceWithAmount(
        this.fromFabric(resource.resource), resource.amount
    )
    fun fromFabricWithAmountFluid(resource: ResourceAmount<FluidVariant>) = ResourceWithAmount(
        this.fromFabric(resource.resource), resource.amount
    )

    fun toFabricVariant(item: StackableItem): ItemVariant {
        if (item.isEmpty) {
            return ItemVariant.blank()
        }

        return ItemVariant.of(item.item, item.tag)
    }
    fun toFabricVariant(fluid: StackableFluid): FluidVariant {
        if (fluid.isEmpty) {
            return FluidVariant.blank()
        }

        return FluidVariant.of(fluid.fluid, fluid.tag)
    }

    fun toFabricWithAmountItem(resource: ResourceWithAmount<StackableItem>) = ResourceAmount(
        this.toFabricVariant(resource.resource), resource.amount
    )
    fun toFabricWithAmountFluid (resource: ResourceWithAmount<StackableFluid>) = ResourceAmount(
        this.toFabricVariant(resource.resource), resource.amount
    )
}