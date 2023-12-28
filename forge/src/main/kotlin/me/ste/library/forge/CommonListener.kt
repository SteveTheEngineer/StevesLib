package me.ste.library.forge

import me.ste.library.StevesLib
import me.ste.library.event.StevesLibEntityEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = StevesLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object CommonListener {
    @SubscribeEvent
    fun onStartTracking(event: PlayerEvent.StartTracking) {
        StevesLibEntityEvent.PAIRING_ADD.invoker().change(event.target, event.entity as? ServerPlayer ?: return)
    }

    @SubscribeEvent
    fun onStopTracking(event: PlayerEvent.StopTracking) {
        StevesLibEntityEvent.PAIRING_REMOVE.invoker().change(event.target, event.entity as? ServerPlayer ?: return)
    }
}