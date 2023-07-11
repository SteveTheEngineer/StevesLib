package me.ste.library.transfer.forge.capability

import me.ste.library.transfer.energy.EnergyContainerItem
import me.ste.library.transfer.forge.adapter.EnergyContainerForgeItemAdapter
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import java.util.function.Supplier

class EnergyContainerItemCapabilityProvider(
    private val stack: ItemStack,
    private val container: Supplier<EnergyContainerItem<*>?>
) : ICapabilityProvider {
    private var adapter: EnergyContainerForgeItemAdapter? = null

    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability != ForgeCapabilities.ENERGY) {
            return LazyOptional.empty()
        }

        val container = this.container.get()
            ?: return LazyOptional.empty()

        if (!container.canAccept(null) && !container.canOutput(null)) {
            return LazyOptional.empty()
        }

        return LazyOptional.of {
            this.adapter ?: {
                val adapter = EnergyContainerForgeItemAdapter(this.stack, container)
                this.adapter = adapter
                adapter
            }
        }.cast()
    }
}