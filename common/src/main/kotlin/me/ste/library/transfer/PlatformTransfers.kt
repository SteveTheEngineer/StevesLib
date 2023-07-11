package me.ste.library.transfer

import me.ste.library.internals.TransferProviders
import me.ste.library.simple.lookup.SimpleBlockLookup
import me.ste.library.simple.lookup.SimpleItemLookup
import me.ste.library.lookup.block.BlockLookup
import me.ste.library.lookup.block.MutableBlockEntityLookup
import me.ste.library.lookup.item.ItemLookup
import me.ste.library.transfer.energy.SimulatableEnergyContainer
import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.item.SimulatableItemContainer
import me.ste.library.transfer.provider.ContainerBlockEntity
import me.ste.library.transfer.provider.ContainerItem

object PlatformTransfers {
    val ITEMS = TransferBlockLookup(TransferProviders::getItems, TransferProviders.getItemsRegistry())
    val FLUIDS = TransferBlockLookup(TransferProviders::getFluids, TransferProviders.getFluidsRegistry())
    val ENERGY = TransferBlockLookup(TransferProviders::getEnergy, TransferProviders.getEnergyRegistry())

    val FLUIDS_ITEM = TransferItemLookup(TransferProviders::getFluids, TransferProviders::registerFluidItem, TransferProviders::registerFluidItem)
    val ENERGY_ITEM = TransferItemLookup(TransferProviders::getEnergy, TransferProviders::registerEnergyItem, TransferProviders::registerEnergyItem)

    init {
        ITEMS.registerBlockEntity { (it as? ContainerBlockEntity)?.itemContainer }
        FLUIDS.registerBlockEntity { (it as? ContainerBlockEntity)?.fluidContainer }
        ENERGY.registerBlockEntity { (it as? ContainerBlockEntity)?.energyContainer }

        FLUIDS_ITEM.register { (it.item as? ContainerItem)?.getFluidContainer(it) }
        ENERGY_ITEM.register { (it.item as? ContainerItem)?.getEnergyContainer(it) }
    }
}