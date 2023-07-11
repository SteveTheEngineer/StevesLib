package me.ste.library.transfer.energy

import net.minecraft.world.item.ItemStack

interface EnergyContainerItem<S> : SnapshotEnergyContainer<S> {
    fun applyResult(stack: ItemStack)
}