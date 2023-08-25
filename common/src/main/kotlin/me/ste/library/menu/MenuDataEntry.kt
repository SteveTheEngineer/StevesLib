package me.ste.library.menu

import net.minecraft.network.FriendlyByteBuf

interface MenuDataEntry {
    val needsSync: Boolean
    fun markSynced()

    fun write(buf: FriendlyByteBuf)
    fun read(buf: FriendlyByteBuf)
}