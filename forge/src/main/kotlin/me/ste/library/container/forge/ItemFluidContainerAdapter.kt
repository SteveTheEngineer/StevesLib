package me.ste.library.container.forge

import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.capability.IFluidHandlerItem

class ItemFluidContainerAdapter(container: ResourceContainer<FluidResource>, private val holder: ResourceHolder<ItemResource>) : FluidContainerAdapter(container), IFluidHandlerItem {
    override fun getContainer() = this.holder.resource.toStack(this.holder.amount.toInt())
}