package me.ste.library.network.channel.obj

import me.ste.library.network.channel.NetworkChannelConnection
import net.minecraft.network.FriendlyByteBuf
import java.util.function.BiConsumer
import java.util.function.Function

data class ObjectNetworkChannelRegistration<T>(
    val messageId: Int,
    val messageType: Class<T>,

    val decode: Function<FriendlyByteBuf, T>,
    val encode: BiConsumer<T, FriendlyByteBuf>,
    val handle: BiConsumer<T, NetworkChannelConnection>
)
