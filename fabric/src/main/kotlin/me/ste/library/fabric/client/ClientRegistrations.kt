package me.ste.library.fabric.client

import me.ste.library.client.event.StevesLibClientModelEvent
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin
import net.minecraft.server.packs.resources.ResourceManager
import java.util.concurrent.CompletableFuture

object ClientRegistrations {
    fun register() {
        PreparableModelLoadingPlugin.register({ manager, _ -> CompletableFuture.completedFuture(manager) }, this::onInitializeModelLoader)
    }

    private fun onInitializeModelLoader(manager: ResourceManager, context: ModelLoadingPlugin.Context) {
        StevesLibClientModelEvent.PROVIDE_MODELS.invoker().provide(manager, context::addModels)
    }
}