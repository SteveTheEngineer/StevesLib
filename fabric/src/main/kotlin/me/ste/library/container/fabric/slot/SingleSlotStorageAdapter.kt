package me.ste.library.container.fabric.slot

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.fabric.TransactionContextWrapper
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.world.item.ItemStack

class SingleSlotStorageAdapter(
    private val holder: ResourceHolder<ItemResource>
) : SingleSlotStorage<ItemVariant> {
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        return this.holder.accept(ItemResource(resource.item, resource.nbt), maxAmount, TransactionContextWrapper(transaction))
    }

    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        if (this.holder.resource != ItemResource(resource.item, resource.nbt)) {
            return 0L
        }

        return this.holder.output(maxAmount, TransactionContextWrapper(transaction))
    }

    override fun isResourceBlank() = this.holder.isEmpty

    override fun getResource() = ItemVariant.of(this.holder.resource.obj, this.holder.resource.tag)

    override fun getAmount() = this.holder.amount

    override fun getCapacity() = this.holder.capacity
}