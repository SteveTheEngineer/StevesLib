package me.ste.library.transfer.forge.capability

import me.ste.library.transfer.energy.SnapshotEnergyContainer
import me.ste.library.transfer.forge.adapter.EnergyContainerForgeAdapter
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import java.util.function.Supplier

class EnergyContainerCapabilityProvider(
    private val container: Supplier<SnapshotEnergyContainer<*>?>
) : ICapabilityProvider {
    private val adapters = mutableMapOf<Direction?, EnergyContainerForgeAdapter>()

    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability != ForgeCapabilities.ENERGY) {
            return LazyOptional.empty()
        }

        val container = this.container.get()
            ?: return LazyOptional.empty()

        if (!container.canAccept(side) && !container.canOutput(side)) {
            return LazyOptional.empty()
        }

        return LazyOptional.of {
            this.adapters.computeIfAbsent(side) { EnergyContainerForgeAdapter(container, it) }
        }.cast()
    }
}