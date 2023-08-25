package me.ste.library.container.fabric

import me.ste.library.container.EnergyContainer
import me.ste.library.container.fabric.reverse.EnergyContainerReverseAdapter
import me.ste.library.container.fabric.reverse.ResourceContainerReverseAdapter
import me.ste.library.container.fabric.slot.SingleSlotStorageAdapter
import me.ste.library.container.fabric.slot.SingleSlotStorageReverseAdapter
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.internal.PlatformContainerProxies
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.core.Direction
import team.reborn.energy.api.EnergyStorage

object PlatformContainerRegistrations {
    fun register() {
        this.registerAdapters()
        this.registerReverseAdapters()
    }

    private fun registerAdapters() {
        ItemStorage.SIDED.registerFallback { level, pos, state, entity, side ->
            val container = PlatformContainerProxies.ITEMS.queryBlock(level, pos, side, state, entity)
                    ?: return@registerFallback null

            ResourceContainerAdapter(container, { ItemVariant.of(it.obj, it.tag) }, { ItemResource(it.item, it.nbt) })
        }

        FluidStorage.SIDED.registerFallback { level, pos, state, entity, side ->
            val container = PlatformContainerProxies.FLUIDS.queryBlock(level, pos, side, state, entity)
                    ?: return@registerFallback null

            ResourceContainerAdapter(container, { FluidVariant.of(it.obj, it.tag) }, { FluidResource(it.fluid, it.nbt) })
        }

        EnergyStorage.SIDED.registerFallback { level, pos, state, entity, side ->
            val container = PlatformContainerProxies.ENERGY.queryBlock(level, pos, side, state, entity)
                    ?: return@registerFallback null

            EnergyContainerAdapter(container)
        }

        FluidStorage.ITEM.registerFallback { stack, context ->
            val holder = SingleSlotStorageReverseAdapter(context.mainSlot)
            val container = PlatformContainerProxies.ITEM_FLUIDS.queryItemStack(holder)
                    ?: return@registerFallback null

            ResourceContainerAdapter(container, { FluidVariant.of(it.obj, it.tag) }, { FluidResource(it.fluid, it.nbt) })
        }

        EnergyStorage.ITEM.registerFallback { stack, context ->
            val holder = SingleSlotStorageReverseAdapter(context.mainSlot)
            val container = PlatformContainerProxies.ITEM_ENERGY.queryItemStack(holder)
                    ?: return@registerFallback null

            EnergyContainerAdapter(container)
        }
    }

    private fun registerReverseAdapters() {
        PlatformContainerProxies.ITEMS.setPlatformProvider { level, pos, side, state, entity ->
            val storage = ItemStorage.SIDED.find(level, pos, state.get(), entity.get(), side ?: Direction.DOWN)
                ?: return@setPlatformProvider null

            if (storage is ResourceContainerAdapter<*, *>) {
                return@setPlatformProvider storage.container as ResourceContainer<ItemResource>
            }

            ResourceContainerReverseAdapter(storage, { ItemResource(it.item, it.nbt) }, { ItemVariant.of(it.obj, it.tag) })
        }

        PlatformContainerProxies.FLUIDS.setPlatformProvider { level, pos, side, state, entity ->
            val storage = FluidStorage.SIDED.find(level, pos, state.get(), entity.get(), side ?: Direction.DOWN)
                ?: return@setPlatformProvider null

            if (storage is ResourceContainerAdapter<*, *>) {
                return@setPlatformProvider storage.container as ResourceContainer<FluidResource>
            }

            ResourceContainerReverseAdapter(storage, { FluidResource(it.fluid, it.nbt) }, { FluidVariant.of(it.obj, it.tag) })
        }

        PlatformContainerProxies.ENERGY.setPlatformProvider { level, pos, side, state, entity ->
            val storage = EnergyStorage.SIDED.find(level, pos, state.get(), entity.get(), side ?: Direction.DOWN)
                ?: return@setPlatformProvider null

            if (storage is EnergyContainerAdapter) {
                return@setPlatformProvider storage.container
            }

            EnergyContainerReverseAdapter(storage)
        }

        PlatformContainerProxies.ITEM_FLUIDS.setPlatformProvider { holder ->
            val slot = SingleSlotStorageAdapter(holder)

            val context = ContainerItemContext.ofSingleSlot(slot)

            val storage = FluidStorage.ITEM.find(holder.resource.toStack(holder.amount.toInt()), context) ?: return@setPlatformProvider null

            if (storage is ResourceContainerAdapter<*, *>) {
                return@setPlatformProvider storage.container as ResourceContainer<FluidResource>
            }

            ResourceContainerReverseAdapter(storage, { FluidResource(it.fluid, it.nbt) }, { FluidVariant.of(it.obj, it.tag) })
        }

        PlatformContainerProxies.ITEM_ENERGY.setPlatformProvider { holder ->
            val slot = SingleSlotStorageAdapter(holder)

            val context = ContainerItemContext.ofSingleSlot(slot)

            val storage = EnergyStorage.ITEM.find(holder.resource.toStack(holder.amount.toInt()), context) ?: return@setPlatformProvider null

            if (storage is EnergyContainerAdapter) {
                return@setPlatformProvider storage.container
            }

            EnergyContainerReverseAdapter(storage)
        }
    }
}