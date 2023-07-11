package me.ste.library.fabric

import me.ste.library.StevesLib
import me.ste.library.transfer.fabric.adapter.*
import me.ste.library.transfer.provider.ContainerBlockEntity
import me.ste.library.transfer.provider.ContainerItem
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import team.reborn.energy.api.EnergyStorage

object StevesLibFabric : ModInitializer {
    override fun onInitialize() {
        StevesLib.init()

        ItemStorage.SIDED.registerFallback { _, _, _, entity, side ->
            val provider = entity as? ContainerBlockEntity ?: return@registerFallback null
            val container = provider.itemContainer ?: return@registerFallback null

            if (!container.canAccept(side) && !container.canOutput(side)) {
                return@registerFallback null
            }

            ItemContainerFabricAdapter(container, side)
        }
        FluidStorage.SIDED.registerFallback { _, _, _, entity, side ->
            val provider = entity as? ContainerBlockEntity ?: return@registerFallback null
            val container = provider.fluidContainer ?: return@registerFallback null

            if (!container.canAccept(side) && !container.canOutput(side)) {
                return@registerFallback null
            }

            FluidContainerFabricAdapter(container, side)
        }
        EnergyStorage.SIDED.registerFallback { _, _, _, entity, side ->
            val provider = entity as? ContainerBlockEntity ?: return@registerFallback null
            val container = provider.energyContainer ?: return@registerFallback null

            if (!container.canAccept(side) && !container.canOutput(side)) {
                return@registerFallback null
            }

            EnergyContainerFabricAdapter(container, side)
        }

        FluidStorage.ITEM.registerFallback { stack, context ->
            val provider = stack.item as? ContainerItem ?: return@registerFallback null
            val container = provider.getFluidContainer(stack) ?: return@registerFallback null

            if (!container.canAccept(null) && !container.canOutput(null)) {
                return@registerFallback null
            }

            FluidContainerFabricItemAdapter(context, container)
        }
        EnergyStorage.ITEM.registerFallback { stack, context ->
            val provider = stack.item as? ContainerItem ?: return@registerFallback null
            val container = provider.getEnergyContainer(stack) ?: return@registerFallback null

            if (!container.canAccept(null) && !container.canOutput(null)) {
                return@registerFallback null
            }

            EnergyContainerFabricItemAdapter(context, container)
        }
    }
}