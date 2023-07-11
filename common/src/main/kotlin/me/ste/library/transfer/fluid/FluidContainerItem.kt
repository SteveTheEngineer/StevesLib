package me.ste.library.transfer.fluid

import net.minecraft.world.item.ItemStack

interface FluidContainerItem<S> : SnapshotFluidContainer<S> {
    fun getResult(): ItemStack
}