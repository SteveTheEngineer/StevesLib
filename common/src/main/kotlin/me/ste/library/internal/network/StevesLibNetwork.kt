package me.ste.library.internal.network

import dev.architectury.networking.NetworkManager
import me.ste.library.StevesLib
import me.ste.library.network.NetworkChannel
import net.minecraft.resources.ResourceLocation

object StevesLibNetwork {
    val CHANNEL = NetworkChannel(
        ResourceLocation(StevesLib.MOD_ID, "channel")
    )

    fun register() {
        CHANNEL.registerReceivers()

        CHANNEL.register(0, NetworkManager.Side.S2C, ::MenuDataMessageS2C)
    }
}