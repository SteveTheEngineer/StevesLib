package me.ste.library.internal.client

import dev.architectury.event.events.client.ClientPlayerEvent
import me.ste.library.network.StevesLibNetwork
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer

object ClientListener {
    fun register() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(this::onClientPlayerQuit)
    }

    private fun onClientPlayerQuit(player: LocalPlayer?) {
        if (!Minecraft.getInstance().isLocalServer) {
            StevesLibNetwork.CHANNELS_I2RL.clear()
            StevesLibNetwork.CHANNELS_RL2I.clear()
        }
    }
}