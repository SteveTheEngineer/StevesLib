package me.ste.library.fabric

import me.ste.library.event.StevesLibEntityEvent
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

object CommonRegistrations {
    fun register() {
        EntityTrackingEvents.STOP_TRACKING.register(this::onStopTracking)
    }

    private fun onStopTracking(entity: Entity, player: ServerPlayer) {
        StevesLibEntityEvent.PAIRING_REMOVE.invoker().change(entity, player)
    }
}