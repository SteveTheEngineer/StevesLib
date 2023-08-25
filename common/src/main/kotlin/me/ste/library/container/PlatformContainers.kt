package me.ste.library.container

import me.ste.library.internal.PlatformContainerProxies
import me.ste.library.lookup.platform.PlatformBlockLookup
import me.ste.library.lookup.platform.PlatformItemLookup

object PlatformContainers {
    val ITEMS = PlatformBlockLookup(PlatformContainerProxies.ITEMS)
    val FLUIDS = PlatformBlockLookup(PlatformContainerProxies.FLUIDS)
    val ENERGY = PlatformBlockLookup(PlatformContainerProxies.ENERGY)

    val ITEM_FLUIDS = PlatformItemLookup(PlatformContainerProxies.ITEM_FLUIDS)
    val ITEM_ENERGY = PlatformItemLookup(PlatformContainerProxies.ITEM_ENERGY)
}