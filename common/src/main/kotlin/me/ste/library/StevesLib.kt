package me.ste.library

import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientChatEvent
import dev.architectury.platform.Platform
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.utils.Env
import me.ste.library.container.PlatformContainers
import me.ste.library.container.provider.ContainerProviderBlockEntity
import me.ste.library.container.provider.ContainerProviderItem
import me.ste.library.internal.CommonListener
import me.ste.library.internal.client.StevesLibClient
import me.ste.library.network.PacketSinks
import me.ste.library.network.StevesLibConnection
import me.ste.library.network.builtin.StevesLibBuiltinChannel
import me.ste.library.network.channel.NetworkChannelConnection
import me.ste.library.network.channel.obj.NetworkMessage
import me.ste.library.network.channel.obj.ObjectNetworkChannel
import me.ste.library.network.channel.obj.ServerNetworkMessage
import net.fabricmc.api.EnvType
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.apache.logging.log4j.LogManager

object StevesLib {
    const val MOD_ID = "steveslib"

    val LOGGER = LogManager.getLogger(this)

    val REGISTRIES by lazy {
        RegistrarManager.get(MOD_ID)
    }

    val ITEM_REGISTRY by lazy {
        REGISTRIES.get(Registries.ITEM)
    }
    val FLUID_REGISTRY by lazy {
        REGISTRIES.get(Registries.FLUID)
    }

    fun init() {
        PlatformContainers.ITEMS.register { level, pos, side, state, lazyEntity ->
            val entity = lazyEntity.get() ?: return@register null
            (entity as? ContainerProviderBlockEntity)?.getItemContainer(side)
        }
        PlatformContainers.FLUIDS.register { level, pos, side, state, lazyEntity ->
            val entity = lazyEntity.get() ?: return@register null
            (entity as? ContainerProviderBlockEntity)?.getFluidContainer(side)
        }
        PlatformContainers.ENERGY.register { level, pos, side, state, lazyEntity ->
            val entity = lazyEntity.get() ?: return@register null
            (entity as? ContainerProviderBlockEntity)?.getEnergyContainer(side)
        }

        PlatformContainers.ITEM_FLUIDS.register {
            (it.resource.obj as? ContainerProviderItem)?.getFluidContainer(it)
        }
        PlatformContainers.ITEM_ENERGY.register {
            (it.resource.obj as? ContainerProviderItem)?.getEnergyContainer(it)
        }

        CommonListener.register()

        StevesLibBuiltinChannel.register()

        if (Platform.getEnv() == EnvType.CLIENT) {
            StevesLibClient.init()
        }
    }
}
