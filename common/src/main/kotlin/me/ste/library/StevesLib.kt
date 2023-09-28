package me.ste.library

import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientChatEvent
import dev.architectury.platform.Platform
import dev.architectury.registry.registries.Registries
import dev.architectury.utils.Env
import me.ste.library.container.PlatformContainers
import me.ste.library.container.provider.ContainerProviderBlockEntity
import me.ste.library.container.provider.ContainerProviderItem
import me.ste.library.network.StevesLibConnection
import me.ste.library.network.builtin.StevesLibBuiltinChannel
import me.ste.library.network.channel.NetworkChannelConnection
import me.ste.library.network.channel.obj.NetworkMessage
import me.ste.library.network.channel.obj.ObjectNetworkChannel
import net.fabricmc.api.EnvType
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.apache.logging.log4j.LogManager

object StevesLib {
    const val MOD_ID = "steveslib"

    val LOGGER = LogManager.getLogger(this)

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

        StevesLibBuiltinChannel.register()
        class TestMessage : NetworkMessage {
            private val stack: ItemStack

            constructor(buf: FriendlyByteBuf) {
                this.stack = buf.readItem()
            }

            constructor(stack: ItemStack) {
                this.stack = stack
            }

            override fun encode(buf: FriendlyByteBuf) {
                buf.writeItem(this.stack)
            }

            override fun handle(channelConnection: NetworkChannelConnection) {
                val connection = channelConnection.connection
                val player = connection.player ?: return
                player.inventory.add(this.stack.copy())
            }
        }

        val channel = ObjectNetworkChannel(ResourceLocation(MOD_ID, "example"), 0)
        channel.registerMessage(0, Env.SERVER, ::TestMessage)
        channel.register()

        if (Platform.getEnv() == EnvType.CLIENT) {
            ClientChatEvent.SEND.register { _, _ ->
                val handler = Minecraft.getInstance().connection
                    ?: return@register EventResult.pass()

                val connection = StevesLibConnection.get(handler.connection)

                channel.sendMessage(connection, TestMessage(ItemStack(Items.DIAMOND, 64)))

                EventResult.pass()
            }
        }
    }
}
