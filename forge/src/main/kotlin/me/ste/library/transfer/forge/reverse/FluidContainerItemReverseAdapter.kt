package me.ste.library.transfer.forge.reverse

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import java.util.function.Consumer
import java.util.function.Supplier

class FluidContainerItemReverseAdapter(
    private val handlerItem: Supplier<IFluidHandlerItem?>,
    private val updateItem: Consumer<ItemStack>
) : FluidContainerReverseAdapter({ handlerItem.get() }) {
    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        val result = super.accept(side, resource)

        val handler = this.handlerItem.get()
        if (handler != null) {
            this.updateItem.accept(handler.container)
        }

        return result
    }

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        val result = super.output(side, resource)

        val handler = this.handlerItem.get()
        if (handler != null) {
            this.updateItem.accept(handler.container)
        }

        return result
    }
}