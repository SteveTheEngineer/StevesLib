package me.ste.library.internal

import me.ste.library.container.EnergyContainer
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.lookup.platform.PlatformBlockLookupProxy
import me.ste.library.lookup.platform.PlatformItemLookupProxy
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource

object PlatformContainerProxies {
    val ITEMS = PlatformBlockLookupProxy<ResourceContainer<ItemResource>>()
    val FLUIDS = PlatformBlockLookupProxy<ResourceContainer<FluidResource>>()
    val ENERGY = PlatformBlockLookupProxy<EnergyContainer>()

    val ITEM_FLUIDS = PlatformItemLookupProxy<ResourceContainer<FluidResource>>()
    val ITEM_ENERGY = PlatformItemLookupProxy<EnergyContainer>()
}