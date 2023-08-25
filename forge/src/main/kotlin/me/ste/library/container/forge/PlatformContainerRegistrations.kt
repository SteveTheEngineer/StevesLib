package me.ste.library.container.forge

import me.ste.library.container.forge.item.ItemEnergyContainerReverseAdapter
import me.ste.library.container.forge.item.ItemFluidContainerReverseAdapter
import me.ste.library.container.forge.reverse.EnergyContainerReverseAdapter
import me.ste.library.container.forge.reverse.FluidContainerReverseAdapter
import me.ste.library.container.forge.reverse.ItemContainerReverseAdapter
import me.ste.library.internal.PlatformContainerProxies
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.ForgeCapabilities

object PlatformContainerRegistrations {
    fun register() {
        PlatformContainerProxies.ITEMS.setPlatformProvider { _, _, side, _, lazyEntity ->
            val entity = lazyEntity.get()
                ?: return@setPlatformProvider null

            val storage = (entity as BlockEntity).getCapability(ForgeCapabilities.ITEM_HANDLER, side).orElse(null)
                ?: return@setPlatformProvider null

            if (storage is ItemContainerAdapter) {
                return@setPlatformProvider storage.container
            }

            ItemContainerReverseAdapter(storage)
        }

        PlatformContainerProxies.FLUIDS.setPlatformProvider { _, _, side, _, lazyEntity ->
            val entity = lazyEntity.get()
                ?: return@setPlatformProvider null

            val storage = (entity as BlockEntity).getCapability(ForgeCapabilities.FLUID_HANDLER, side).orElse(null)
                ?: return@setPlatformProvider null

            if (storage is FluidContainerAdapter) {
                return@setPlatformProvider storage.container
            }

            FluidContainerReverseAdapter(storage)
        }

        PlatformContainerProxies.ENERGY.setPlatformProvider { _, _, side, _, lazyEntity ->
            val entity = lazyEntity.get()
                ?: return@setPlatformProvider null

            val storage = (entity as BlockEntity).getCapability(ForgeCapabilities.ENERGY, side).orElse(null)
                ?: return@setPlatformProvider null

            if (storage is EnergyContainerAdapter) {
                return@setPlatformProvider storage.container
            }

            EnergyContainerReverseAdapter(storage)
        }

        PlatformContainerProxies.ITEM_FLUIDS.setPlatformProvider { holder ->
            val stack = holder.resource.toStack(holder.amount.toInt()) as ItemStack

            val container = PlatformContainerProxies.ITEM_FLUIDS.queryItemStack(holder)
            if (container != null) {
                return@setPlatformProvider container
            }

            val storage = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null)
                ?: return@setPlatformProvider null

            ItemFluidContainerReverseAdapter(storage, holder)
        }

        PlatformContainerProxies.ITEM_ENERGY.setPlatformProvider { holder ->
            val stack = holder.resource.toStack(holder.amount.toInt()) as ItemStack

            val container = PlatformContainerProxies.ITEM_ENERGY.queryItemStack(holder)
            if (container != null) {
                return@setPlatformProvider container
            }

            val storage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null)
                ?: return@setPlatformProvider null

            if (storage is EnergyContainerAdapter) {
                return@setPlatformProvider storage.container
            }

            ItemEnergyContainerReverseAdapter(storage, stack, holder)
        }
    }
}