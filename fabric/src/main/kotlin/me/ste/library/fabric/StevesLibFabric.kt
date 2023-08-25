package me.ste.library.fabric

import me.ste.library.StevesLib
import me.ste.library.container.PlatformContainers
import me.ste.library.container.fabric.PlatformContainerRegistrations
import net.fabricmc.api.ModInitializer

object StevesLibFabric : ModInitializer {
    override fun onInitialize() {
        StevesLib.init()
        PlatformContainerRegistrations.register()
    }
}
