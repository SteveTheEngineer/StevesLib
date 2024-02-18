package me.ste.library.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

interface StevesLibEntityEvent {
    companion object {
        /**
         * Called after the spawn packet is sent.
         */
        val PAIRING_ADD: Event<PairingChange> = EventFactory.createLoop()

        /**
         * Called after the remove packet is sent.
         */
        val PAIRING_REMOVE: Event<PairingChange> = EventFactory.createLoop()
    }

    fun interface PairingChange {
        fun change(entity: Entity, player: ServerPlayer)
    }
}