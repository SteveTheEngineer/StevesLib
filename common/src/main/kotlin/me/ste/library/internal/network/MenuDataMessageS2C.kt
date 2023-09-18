package me.ste.library.internal.network

import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import me.ste.library.menu.MenuDataProvider
import me.ste.library.network.NetworkMessage
import net.minecraft.network.FriendlyByteBuf

class MenuDataMessageS2C : NetworkMessage {
    val containerId: Int
    val data: Map<Int, ByteArray>

    constructor(containerId: Int, data: Map<Int, ByteArray>) {
        this.containerId = containerId
        this.data = data
    }

    constructor(buf: FriendlyByteBuf) {
        this.containerId = buf.readVarInt()
        this.data = buf.readMap(FriendlyByteBuf::readVarInt, FriendlyByteBuf::readByteArray)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeVarInt(this.containerId)
        buf.writeMap(this.data, FriendlyByteBuf::writeVarInt, FriendlyByteBuf::writeByteArray)
    }

    override fun handle(context: NetworkManager.PacketContext) { // TODO something broken here
        context.queue {
            if (context.player.containerMenu.containerId != this.containerId) {
                return@queue
            }

            val menu = context.player.containerMenu as? MenuDataProvider
                ?: return@queue

            val data = menu.data

            for ((index, array) in this.data) {
                val entry = data.getOrNull(index) ?: continue
                val buf = FriendlyByteBuf(Unpooled.wrappedBuffer(array))

                entry.read(buf)
            }
        }
    }
}