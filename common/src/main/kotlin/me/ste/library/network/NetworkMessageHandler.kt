package me.ste.library.network

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf

abstract class NetworkMessageHandler<T>(
    val id: Int,
    val clazz: Class<T>,
    val side: NetworkManager.Side
) {
    abstract fun encode(message: T, buf: FriendlyByteBuf)
    abstract fun decode(buf: FriendlyByteBuf): T
    abstract fun handle(message: T, context: NetworkManager.PacketContext)
}
