package me.ste.library.network2

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import me.ste.library.internal.network2.StevesLibConnection
import net.minecraft.server.network.ServerLoginPacketListenerImpl

object StevesLibNetworkEvent {
    val LOGIN_READY_TO_ACCEPT: Event<Interruptable> = EventFactory.createLoop()
    val LOGIN_TOOK_TOO_LONG: Event<Interruptable> = EventFactory.createLoop()
    val LOGIN_CONNECTION_READY: Event<ConnectionReady> = EventFactory.createLoop()

    fun interface Interruptable {
        fun process(listener: ServerLoginPacketListenerImpl, interrupt: Runnable)
    }

    fun interface ConnectionReady {
        fun ready(connection: StevesLibConnection)
    }
}