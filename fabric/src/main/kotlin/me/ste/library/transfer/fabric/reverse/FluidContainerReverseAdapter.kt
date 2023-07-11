package me.ste.library.transfer.fabric.reverse

import me.ste.library.fabric.ResourceConversionsFabric
import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.Direction
import java.util.function.Function

open class FluidContainerReverseAdapter(
    private val storages: Function<Direction?, Storage<FluidVariant>?>
) : SimulatableFluidContainer {
    private fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>, simulate: Boolean): Long {
        val storage = this.storages.apply(side) ?: return 0L
        val variant = ResourceConversionsFabric.toFabricVariant(resource.resource)

        return Transaction.openOuter().use {
            val amount = storage.insert(variant, resource.amount, it)

            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }

            amount
        }
    }

    private fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>, simulate: Boolean): Long {
        val storage = this.storages.apply(side) ?: return 0L
        val variant = ResourceConversionsFabric.toFabricVariant(resource.resource)

        return Transaction.openOuter().use {
            val amount = storage.extract(variant, resource.amount, it)

            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }

            amount
        }
    }

    override fun simulateAccept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.accept(side, resource, true)

    override fun simulateOutput(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.output(side, resource, true)

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.accept(side, resource, false)

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>) = this.output(side, resource, false)

    override fun getContainerSize(side: Direction?): Int {
        val storage = this.storages.apply(side) ?: return 0
        return storage.count() + 1
    }

    override fun getResource(side: Direction?, slot: Int): ResourceWithAmount<StackableFluid> {
        val storage = this.storages.apply(side) ?: return ResourceWithAmount.EMPTY_FLUID
        val view = storage.elementAtOrNull(slot) ?: return ResourceWithAmount.EMPTY_FLUID

        return ResourceWithAmount(
            ResourceConversionsFabric.fromFabric(view.resource), view.amount
        )
    }

    override fun getCapacity(side: Direction?, slot: Int): Long {
        if (slot < 0) {
            return 0L
        }

        val storage = this.storages.apply(side) ?: return 0L

        var count = 0
        val iterator = storage.iterator()
        while (iterator.hasNext()) {
            val view = iterator.next()

            if (count == slot) {
                return view.capacity
            }

            count++
        }

        if (count == slot) {
            return Long.MAX_VALUE
        }

        return 0L
    }

    override fun canAccept(side: Direction?, slot: Int, resource: StackableFluid) = this.storages.apply(side)?.supportsInsertion() == true

    override fun canAccept(side: Direction?) = this.storages.apply(side)?.supportsInsertion() == true

    override fun canOutput(side: Direction?) = this.storages.apply(side)?.supportsExtraction() == true
}