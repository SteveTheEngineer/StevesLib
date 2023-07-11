package me.ste.library.simple

import me.ste.library.lookup.block.BlockLookup
import me.ste.library.lookup.item.ItemLookup
import me.ste.library.simple.wrapper.DropletFluidContainerWrapper
import me.ste.library.simple.wrapper.MillibucketFluidContainerWrapper
import me.ste.library.simple.wrapper.StackItemContainerWrapper
import me.ste.library.transfer.PlatformTransfers

object PlatformTransferWrappers {
    val ITEM_STACKS = BlockLookup { level, pos ->
        val container = PlatformTransfers.ITEMS.get(level, pos) ?: return@BlockLookup null
        StackItemContainerWrapper(container)
    }

    val FLUID_MILLIBUCKETS = BlockLookup { level, pos ->
        val container = PlatformTransfers.FLUIDS.get(level, pos) ?: return@BlockLookup null
        MillibucketFluidContainerWrapper(container)
    }

    val FLUID_DROPLETS = BlockLookup { level, pos ->
        val container = PlatformTransfers.FLUIDS.get(level, pos) ?: return@BlockLookup null
        DropletFluidContainerWrapper(container)
    }

    val FLUID_MILLIBUCKETS_ITEM = ItemLookup { stack, setStack ->
        val container = PlatformTransfers.FLUIDS_ITEM.get(stack, setStack) ?: return@ItemLookup null
        MillibucketFluidContainerWrapper(container)
    }

    val FLUID_DROPLETS_ITEM = ItemLookup { stack, setStack ->
        val container = PlatformTransfers.FLUIDS_ITEM.get(stack, setStack) ?: return@ItemLookup null
        DropletFluidContainerWrapper(container)
    }
}