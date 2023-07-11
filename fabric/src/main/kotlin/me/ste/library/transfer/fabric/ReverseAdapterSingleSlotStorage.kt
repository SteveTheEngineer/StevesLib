package me.ste.library.transfer.fabric

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class ReverseAdapterSingleSlotStorage(
    initialStack: ItemStack,
    private val setStack: Consumer<ItemStack>
) : SingleItemStorage() {
    init {
        this.variant = ItemVariant.of(initialStack)
        this.amount = initialStack.count.toLong()
    }

    override fun getCapacity(variant: ItemVariant) = variant.item.maxStackSize.toLong()

    override fun onFinalCommit() {
        this.setStack.accept(this.variant.toStack(this.amount.toInt()))
    }
}