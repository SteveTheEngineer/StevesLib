package me.ste.library.network.channel.obj

import me.ste.library.network.channel.NetworkChannelConnection
import net.minecraft.network.FriendlyByteBuf

interface NetworkMessage {
    fun encode(buf: FriendlyByteBuf)
    fun handle(connection: NetworkChannelConnection)
}