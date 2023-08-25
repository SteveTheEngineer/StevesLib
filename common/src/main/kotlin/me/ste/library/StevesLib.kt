package me.ste.library

import dev.architectury.event.CompoundEventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.registry.registries.Registries
import me.ste.library.container.EnergyContainer
import me.ste.library.container.PlatformContainers
import me.ste.library.simple.SingleSlotConsumer
import me.ste.library.transaction.TransactionShard
import me.ste.library.transaction.Transactions
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items

object StevesLib {
    const val MOD_ID = "steveslib"

    val REGISTRIES by lazy {
        Registries.get(MOD_ID)
    }

    val ITEM_REGISTRY by lazy {
        REGISTRIES.get(Registry.ITEM_REGISTRY)
    }
    val FLUID_REGISTRY by lazy {
        REGISTRIES.get(Registry.FLUID_REGISTRY)
    }

    fun init() {
        
    }
}
