package me.ste.library.fabric.client

import me.ste.library.network.fabric.StevesLibNetworkClient
import net.fabricmc.api.ClientModInitializer

object StevesLibFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        StevesLibNetworkClient.register()
    }
}