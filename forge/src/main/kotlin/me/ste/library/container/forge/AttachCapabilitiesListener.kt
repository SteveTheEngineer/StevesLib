package me.ste.library.container.forge

import me.ste.library.StevesLib
import me.ste.library.container.forge.capability.BlockEntityCapabilityProvider
import me.ste.library.container.forge.capability.ItemStackCapabilityProvider
import me.ste.library.internal.PlatformContainerProxies
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = StevesLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object AttachCapabilitiesListener {
    val KEY = ResourceLocation(StevesLib.MOD_ID, "container")

    @SubscribeEvent
    fun onAttachCapabilitiesBlockEntity(event: AttachCapabilitiesEvent<BlockEntity>) {
        event.addCapability(KEY, BlockEntityCapabilityProvider(event.`object`))
    }

    @SubscribeEvent
    fun onAttachCapabilitiesItemStack(event: AttachCapabilitiesEvent<ItemStack>) {
        event.addCapability(KEY, ItemStackCapabilityProvider(event.`object`))
    }
}