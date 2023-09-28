package me.ste.library.network

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.server.network.ServerLoginPacketListenerImpl

object StevesLibNetworkEvent {
    val LOGIN_READY_TO_ACCEPT: Event<Interruptable> = EventFactory.createLoop()
    val LOGIN_TIMEOUT: Event<Interruptable> = EventFactory.createLoop()

    val CONNECTION_FINAL_STATUS: Event<ConnectionFinalStatus> = EventFactory.createLoop()
    val CONNECTION_END: Event<ConnectionEnd> = EventFactory.createLoop()

    fun interface Interruptable {
        fun process(listener: ServerLoginPacketListenerImpl, interrupt: Runnable)
    }

    fun interface ConnectionFinalStatus {
        fun finalStatus(connection: StevesLibConnection)
    }

    fun interface ConnectionEnd {
        fun end(connection: StevesLibConnection)
    }
}