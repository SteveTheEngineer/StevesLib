package me.ste.library.fabric.client

import net.fabricmc.api.ClientModInitializer

object StevesLibFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientRegistrations.register()
    }
}