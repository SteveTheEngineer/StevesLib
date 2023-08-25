package me.ste.library.container.forge.capability

import me.ste.library.container.forge.EnergyContainerAdapter
import me.ste.library.container.forge.ItemFluidContainerAdapter
import me.ste.library.simple.SingleSlotConsumer
import me.ste.library.internal.PlatformContainerProxies
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

class ItemStackCapabilityProvider(
    private val stack: ItemStack
) : ICapabilityProvider {
    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability == ForgeCapabilities.FLUID_HANDLER_ITEM) {
            val holder = SingleSlotConsumer(this.stack) {}

            val container = PlatformContainerProxies.ITEM_FLUIDS.queryItemStack(holder)
                ?: return LazyOptional.empty()

            if (!container.canAccept && !container.canOutput) {
                return LazyOptional.empty()
            }

            return LazyOptional.of { ItemFluidContainerAdapter(container, holder) }.cast()
        }

        if (capability == ForgeCapabilities.ENERGY) {
            val holder = SingleSlotConsumer(this.stack) {
                if (this.stack.item != it.item) {
                    throw IllegalStateException("Changing the item type is not supported for Forge energy containers.")
                }

                this.stack.tag = it.tag
                this.stack.count = it.count
            }

            val container = PlatformContainerProxies.ITEM_ENERGY.queryItemStack(holder)
                ?: return LazyOptional.empty()

            if (!container.canAccept && !container.canOutput) {
                return LazyOptional.empty()
            }

            return LazyOptional.of { EnergyContainerAdapter(container) }.cast()
        }

        return LazyOptional.empty()
    }
}