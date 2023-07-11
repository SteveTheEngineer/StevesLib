package me.ste.library.transfer.forge.capability

import me.ste.library.transfer.forge.adapter.FluidContainerForgeAdapter
import me.ste.library.transfer.forge.adapter.ItemContainerForgeAdapter
import me.ste.library.transfer.item.SnapshotItemContainer
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import java.util.function.Supplier

class ItemContainerCapabilityProvider(
    private val container: Supplier<SnapshotItemContainer<*>?>
) : ICapabilityProvider {
    private val adapters = mutableMapOf<Direction?, ItemContainerForgeAdapter>()

    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability != ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.empty()
        }

        val container = this.container.get()
            ?: return LazyOptional.empty()

        if (!container.canAccept(side) && !container.canOutput(side)) {
            return LazyOptional.empty()
        }

        return LazyOptional.of {
            this.adapters.computeIfAbsent(side) { ItemContainerForgeAdapter(container, it) }
        }.cast()
    }
}