package me.ste.library.container.forge.capability

import me.ste.library.StevesLib
import me.ste.library.container.forge.EnergyContainerAdapter
import me.ste.library.container.forge.FluidContainerAdapter
import me.ste.library.container.forge.ItemContainerAdapter
import me.ste.library.internal.PlatformContainerProxies
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

class BlockEntityCapabilityProvider(
        private val entity: BlockEntity
) : ICapabilityProvider {
    override fun <T : Any?> getCapability(capability: Capability<T>, side: Direction?): LazyOptional<T> {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            val container = PlatformContainerProxies.ITEMS.queryBlock(this.entity.level!!, this.entity.blockPos, side, this.entity.blockState, this.entity)
                    ?: return LazyOptional.empty()

            if (!container.canAccept && !container.canOutput) {
                return LazyOptional.empty()
            }

            return LazyOptional.of { ItemContainerAdapter(container) }.cast()
        }

        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            val container = PlatformContainerProxies.FLUIDS.queryBlock(this.entity.level!!, this.entity.blockPos, side, this.entity.blockState, this.entity)
                    ?: return LazyOptional.empty()

            if (!container.canAccept && !container.canOutput) {
                return LazyOptional.empty()
            }

            return LazyOptional.of { FluidContainerAdapter(container) }.cast()
        }

        if (capability == ForgeCapabilities.ENERGY) {
            val container = PlatformContainerProxies.ENERGY.queryBlock(this.entity.level!!, this.entity.blockPos, side, this.entity.blockState, this.entity)
                    ?: return LazyOptional.empty()

            if (!container.canAccept && !container.canOutput) {
                return LazyOptional.empty()
            }

            return LazyOptional.of { EnergyContainerAdapter(container) }.cast()
        }

        return LazyOptional.empty()
    }
}