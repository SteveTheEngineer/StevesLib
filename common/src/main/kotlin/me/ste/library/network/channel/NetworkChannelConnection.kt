package me.ste.library.network.channel

import me.ste.library.network.StevesLibConnection
import me.ste.library.network.data.ConnectionDataKey
import net.minecraft.network.FriendlyByteBuf

class NetworkChannelConnection(
    val channel: NetworkChannel,
    val connection: StevesLibConnection
) {
    var status = NetworkChannelConnectionStatus.NEGOTIATING

    fun send(data: FriendlyByteBuf) {
        if (this.status != NetworkChannelConnectionStatus.READY) {
            throw IllegalStateException("Cannot send data on a non-ready network channel connection.")
        }

        this.connection.sendChannelMessage(this.channel.channelId, data)
    }
}