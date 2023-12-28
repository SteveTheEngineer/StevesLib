package me.ste.library.serialization

import net.minecraft.network.FriendlyByteBuf

interface NetworkSerializable {
    fun write(buf: FriendlyByteBuf)
    fun read(buf: FriendlyByteBuf)
}