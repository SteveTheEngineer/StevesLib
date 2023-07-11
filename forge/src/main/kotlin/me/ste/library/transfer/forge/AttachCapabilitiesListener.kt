package me.ste.library.transfer.forge

import me.ste.library.StevesLib
import me.ste.library.internals.forge.TransferProvidersImpl
import me.ste.library.transfer.forge.adapter.EnergyContainerForgeAdapter
import me.ste.library.transfer.forge.capability.*
import me.ste.library.transfer.item.SnapshotItemContainer
import me.ste.library.transfer.provider.ContainerBlockEntity
import me.ste.library.transfer.provider.ContainerItem
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.function.Function

@Mod.EventBusSubscriber(modid = StevesLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object AttachCapabilitiesListener {
    val ITEMS_KEY = ResourceLocation(StevesLib.MOD_ID, "items")
    val FLUIDS_KEY = ResourceLocation(StevesLib.MOD_ID, "fluids")
    val ENERGY_KEY = ResourceLocation(StevesLib.MOD_ID, "energy")

    @SubscribeEvent
    fun onAttachCapabilitiesBlockEntity(event: AttachCapabilitiesEvent<BlockEntity>) {
        val entity = event.`object`
        val type = entity.type

        val items = TransferProvidersImpl.ITEMS_MAP[type]?.apply(entity)
            ?: TransferProvidersImpl.ITEMS.firstNotNullOfOrNull { it.apply(entity) }

        val fluids = TransferProvidersImpl.FLUIDS_MAP[type]?.apply(entity)
            ?: TransferProvidersImpl.FLUIDS.firstNotNullOfOrNull { it.apply(entity) }

        val energy = TransferProvidersImpl.ENERGY_MAP[type]?.apply(entity)
            ?: TransferProvidersImpl.ENERGY.firstNotNullOfOrNull { it.apply(entity) }

        if (items != null) {
            event.addCapability(ITEMS_KEY, ItemContainerCapabilityProvider { items })
        }

        if (fluids != null) {
            event.addCapability(FLUIDS_KEY, FluidContainerCapabilityProvider { fluids })
        }

        if (energy != null) {
            event.addCapability(ENERGY_KEY, EnergyContainerCapabilityProvider { energy })
        }
    }

    @SubscribeEvent
    fun onAttachCapabilitiesItemStack(event: AttachCapabilitiesEvent<ItemStack>) {
        val stack = event.`object`
        val item = stack.item

        val fluids = TransferProvidersImpl.FLUID_ITEMS_MAP[item]?.apply(stack)
            ?: TransferProvidersImpl.FLUID_ITEMS.firstNotNullOfOrNull { it.apply(stack) }

        val energy = TransferProvidersImpl.ENERGY_ITEMS_MAP[item]?.apply(stack)
            ?: TransferProvidersImpl.ENERGY_ITEMS.firstNotNullOfOrNull { it.apply(stack) }

        if (fluids != null) {
            event.addCapability(FLUIDS_KEY, FluidContainerItemCapabilityProvider { fluids })
        }

        if (energy != null) {
            event.addCapability(ENERGY_KEY, EnergyContainerItemCapabilityProvider(stack) { energy })
        }
    }
}