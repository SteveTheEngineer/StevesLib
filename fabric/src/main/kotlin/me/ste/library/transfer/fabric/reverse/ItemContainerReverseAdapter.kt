package me.ste.library.transfer.fabric.reverse

import me.ste.library.fabric.ResourceConversionsFabric
import me.ste.library.transfer.item.SimulatableItemContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableItem
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.Direction
import java.util.function.Function

class ItemContainerReverseAdapter(
    private val storages: Function<Direction?, Storage<ItemVariant>?>
) : SimulatableItemContainer {
    private fun getSlot(side: Direction?, slot: Int): StorageView<ItemVariant>? {
        val storage = this.storages.apply(side) ?: return null
        return storage.elementAtOrNull(slot)
    }

    private fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>, simulate: Boolean): Long {
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

    private fun output(side: Direction?, slot: Int, amount: Long, simulate: Boolean): Long {
        val view = this.getSlot(side, slot) ?: return 0L

        return Transaction.openOuter().use {
            val amount = view.extract(view.resource, amount, it)

            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }

            amount
        }
    }

    override fun simulateAccept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.accept(side, slot, resource, true)

    override fun simulateOutput(side: Direction?, slot: Int, amount: Long) = this.output(side, slot, amount, true)

    override fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>) = this.accept(side, slot, resource, false)

    override fun output(side: Direction?, slot: Int, amount: Long) = this.output(side, slot, amount, false)

    override fun getContainerSize(side: Direction?): Int {
        val storage = this.storages.apply(side) ?: return 0
        return storage.count() + 1
    }

    override fun getResource(side: Direction?, slot: Int): ResourceWithAmount<StackableItem> {
        val view = this.getSlot(side, slot) ?: return ResourceWithAmount.EMPTY_ITEM
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

    override fun canAccept(side: Direction?, slot: Int, resource: StackableItem) = this.storages.apply(side)?.supportsInsertion() == true

    override fun canAccept(side: Direction?) = this.storages.apply(side)?.supportsInsertion() == true

    override fun canOutput(side: Direction?) = this.storages.apply(side)?.supportsExtraction() == true
}