package me.ste.library.container.provider

import me.ste.library.container.EnergyContainer
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource

interface ContainerProviderItem {
    fun getFluidContainer(storage: ResourceHolder<ItemResource>): ResourceContainer<FluidResource>? = null
    fun getEnergyContainer(storage: ResourceHolder<ItemResource>): EnergyContainer? = null
}