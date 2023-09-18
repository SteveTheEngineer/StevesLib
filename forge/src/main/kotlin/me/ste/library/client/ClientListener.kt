package me.ste.library.client

import me.ste.library.StevesLib
import me.ste.library.client.event.ClientModelEvent
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(Dist.CLIENT, modid = StevesLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ClientListener {
    @SubscribeEvent
    fun onModelRegisterAdditional(event: ModelEvent.RegisterAdditional) {
        ClientModelEvent.PROVIDE_MODELS.invoker().provide(Minecraft.getInstance().resourceManager, event::register)
    }
}