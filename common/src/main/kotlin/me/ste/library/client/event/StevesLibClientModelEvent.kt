package me.ste.library.client.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import java.util.function.Consumer

interface StevesLibClientModelEvent {
    companion object {
        val PROVIDE_MODELS: Event<ProvideModels> = EventFactory.createLoop()
    }

    fun interface ProvideModels {
        fun provide(manager: ResourceManager, consumer: Consumer<ResourceLocation>)
    }
}