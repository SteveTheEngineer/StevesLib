package me.ste.library.forge

import dev.architectury.platform.forge.EventBuses
import me.ste.library.StevesLib
import me.ste.library.container.forge.PlatformContainerRegistrations
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.items.ItemStackHandler
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(StevesLib.MOD_ID)
object StevesLibForge {
    init {
        EventBuses.registerModEventBus(StevesLib.MOD_ID, MOD_BUS)
        StevesLib.init()

        PlatformContainerRegistrations.register()
    }
}
