package me.ste.library.transfer.provider

import me.ste.library.transfer.energy.EnergyContainerItem
import me.ste.library.transfer.fluid.FluidContainerItem
import me.ste.library.transfer.item.ItemContainer
import net.minecraft.world.item.ItemStack

interface ContainerItem {
    fun getFluidContainer(stack: ItemStack): FluidContainerItem<*>? = null
    fun getEnergyContainer(stack: ItemStack): EnergyContainerItem<*>? = null
}