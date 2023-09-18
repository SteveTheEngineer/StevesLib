package me.ste.library.fabric

import me.ste.library.StevesLib
import me.ste.library.container.fabric.PlatformContainerRegistrations
import me.ste.library.network.fabric.StevesLibNetworkServer
import net.fabricmc.api.ModInitializer

object StevesLibFabric : ModInitializer {
    override fun onInitialize() {
        StevesLib.init()
        PlatformContainerRegistrations.register()

        StevesLibNetworkServer.register()
    }
}
