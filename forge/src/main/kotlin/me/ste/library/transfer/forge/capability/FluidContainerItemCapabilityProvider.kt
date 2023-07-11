package me.ste.library.transfer.forge.capability

import me.ste.library.transfer.fluid.FluidContainerItem
import me.ste.library.transfer.forge.adapter.FluidContainerForgeItemAdapter
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import java.util.function.Supplier

class FluidContainerItemCapabilityProvider(
    private val container: Supplier<FluidContainerItem<*>?>
) : ICapabilityProvider {
    private var adapter: FluidContainerForgeItemAdapter? = null

    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability != ForgeCapabilities.FLUID_HANDLER_ITEM) {
            return LazyOptional.empty()
        }

        val container = this.container.get()
            ?: return LazyOptional.empty()

        if (!container.canAccept(null) && !container.canOutput(null)) {
            return LazyOptional.empty()
        }

        return LazyOptional.of {
            this.adapter ?: {
                val adapter = FluidContainerForgeItemAdapter(container)
                this.adapter = adapter
                adapter
            }
        }.cast()
    }
}