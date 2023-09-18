package me.ste.library.container.provider

import me.ste.library.container.EnergyContainer
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import net.minecraft.core.Direction

interface ContainerProviderBlockEntity {
    fun getItemContainer(side: Direction?): ResourceContainer<ItemResource>? = null
    fun getFluidContainer(side: Direction?): ResourceContainer<FluidResource>? = null
    fun getEnergyContainer(side: Direction?): EnergyContainer? = null
}