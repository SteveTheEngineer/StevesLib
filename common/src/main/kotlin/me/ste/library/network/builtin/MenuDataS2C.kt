package me.ste.library.network.builtin

import io.netty.buffer.Unpooled
import me.ste.library.menu.MenuDataProvider
import me.ste.library.network.channel.NetworkChannelConnection
import me.ste.library.network.channel.obj.NetworkMessage
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

class MenuDataS2C : NetworkMessage {
    private val containerId: Int
    private val data: Map<Int, ByteArray>

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

    override fun handle(connection: NetworkChannelConnection) {
        Minecraft.getInstance().execute { this.doHandle(connection) }
    }

    private fun doHandle(connection: NetworkChannelConnection) {
        val player = Minecraft.getInstance().player ?: return
        val menu = player.containerMenu

        if (menu.containerId != this.containerId) {
            return
        }

        val provider = menu as MenuDataProvider

        for ((index, value) in this.data) {
            val entry = provider.data[index]
            val buf = FriendlyByteBuf(Unpooled.copiedBuffer(value))

            entry.read(buf)
        }
    }
}