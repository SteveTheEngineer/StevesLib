package me.ste.library.network

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf

interface NetworkMessage {
    fun encode(buf: FriendlyByteBuf)
    fun handle(context: NetworkManager.PacketContext)
}