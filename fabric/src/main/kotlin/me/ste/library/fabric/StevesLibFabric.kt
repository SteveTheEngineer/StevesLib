package me.ste.library.fabric

import me.ste.library.StevesLib
import me.ste.library.container.fabric.PlatformContainerRegistrations
import me.ste.library.fabric.client.ClientRegistrations
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin

object StevesLibFabric : ModInitializer {
    override fun onInitialize() {
        StevesLib.init()
        PlatformContainerRegistrations.register()
        CommonRegistrations.register()
    }
}
