package me.ste.library.transfer.forge.capability

import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.forge.adapter.FluidContainerForgeAdapter
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import java.util.function.Supplier

class FluidContainerCapabilityProvider(
    private val container: Supplier<SnapshotFluidContainer<*>?>
) : ICapabilityProvider {
    private val adapters = mutableMapOf<Direction?, FluidContainerForgeAdapter>()

    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability != ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.empty()
        }

        val container = this.container.get()
            ?: return LazyOptional.empty()

        if (!container.canAccept(side) && !container.canOutput(side)) {
            return LazyOptional.empty()
        }

        return LazyOptional.of {
            this.adapters.computeIfAbsent(side) { FluidContainerForgeAdapter(container, it) }
        }.cast()
    }
}