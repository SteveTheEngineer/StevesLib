package me.ste.library.network

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import java.util.function.Consumer

interface StevesLibNetworkEvent {
    companion object {
        val LOGIN_READY_TO_ACCEPT: Event<LoginEvent> = EventFactory.createEventResult()
        val LOGIN_TIMEOUT: Event<LoginEvent> = EventFactory.createEventResult()

        val CONNECTION_FINAL_STATUS: Event<ConnectionFinalStatus> = EventFactory.createLoop()
        val CONNECTION_END: Event<ConnectionEnd> = EventFactory.createLoop()

        val REGISTER_CHANNELS: Event<RegisterChannels> = EventFactory.createLoop()
    }

    fun interface LoginEvent {
        fun process(listener: ServerLoginPacketListenerImpl): EventResult
    }

    fun interface ConnectionFinalStatus {
        fun finalStatus(connection: StevesLibConnection)
    }

    fun interface ConnectionEnd {
        fun end(connection: StevesLibConnection)
    }

    fun interface RegisterChannels {
        fun register(consumer: Consumer<ResourceLocation>)
    }
}