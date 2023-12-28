package me.ste.library.internal

import dev.architectury.event.events.common.LifecycleEvent
import me.ste.library.network.StevesLibNetwork
import me.ste.library.network.StevesLibNetworkEvent
import net.minecraft.server.MinecraftServer

object CommonListener {
    fun register() {
        LifecycleEvent.SERVER_STARTED.register(this::onServerStarted)
        LifecycleEvent.SERVER_STOPPED.register(this::onServerStopped)
    }

    private fun onServerStopped(server: MinecraftServer) {
        StevesLibNetwork.CHANNELS_I2RL.clear()
        StevesLibNetwork.CHANNELS_RL2I.clear()
    }

    private fun onServerStarted(server: MinecraftServer) {
        var id = 0

        StevesLibNetworkEvent.REGISTER_CHANNELS.invoker().register {
            val channelId = id++

            StevesLibNetwork.CHANNELS_I2RL[channelId] = it
            StevesLibNetwork.CHANNELS_RL2I[it] = channelId
        }
    }
}