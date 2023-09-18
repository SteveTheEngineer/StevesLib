package me.ste.library

import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientChatEvent
import dev.architectury.registry.registries.Registries
import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.container.PlatformContainers
import me.ste.library.container.provider.ContainerProviderBlockEntity
import me.ste.library.container.provider.ContainerProviderItem
import me.ste.library.internal.network.StevesLibNetwork
import me.ste.library.internal.network2.ConnectionStatus
import me.ste.library.internal.network2.StevesLibConnection
import me.ste.library.internal.network2.StevesLibNetworkInternals
import me.ste.library.network2.StevesLibNetworkEvent
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

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
        StevesLibNetwork.register()
        StevesLibNetworkInternals.register()

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

        val channelId = ResourceLocation(MOD_ID, "example")

        ClientChatEvent.SEND.register { str, component ->
            val connection = StevesLibConnection.get(Minecraft.getInstance().connection!!.connection)

            if (connection.status != ConnectionStatus.READY) {
                println("Unable to send the packet due to the connection being in the status ${connection.status}")
                return@register EventResult.pass()
            }

            val buf = FriendlyByteBuf(Unpooled.buffer())

            buf.writeItem(
                ItemStack(Items.DIAMOND, 64)
            )

            connection.sendChannelMessage(channelId, buf)

            EventResult.pass()
        }

        StevesLibNetworkEvent.LOGIN_CONNECTION_READY.register {
            if (it.env != Env.SERVER) {
                return@register
            }

            it.registerHandler(channelId) { buf ->
                val player = it.player ?: return@registerHandler

                val stack = buf.readItem()
                player.inventory.add(stack)
            }
        }
    }
}
