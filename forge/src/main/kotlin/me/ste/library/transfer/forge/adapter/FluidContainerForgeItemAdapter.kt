package me.ste.library.transfer.forge.adapter

import me.ste.library.transfer.fluid.FluidContainerItem
import net.minecraftforge.fluids.capability.IFluidHandlerItem

class FluidContainerForgeItemAdapter(private val itemContainer: FluidContainerItem<*>) : FluidContainerForgeAdapter(itemContainer, null), IFluidHandlerItem {
    override fun getContainer() = this.itemContainer.getResult()
}