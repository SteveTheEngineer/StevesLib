package me.ste.library.network.builtin

import me.ste.library.network.channel.NetworkChannelConnection
import me.ste.library.network.channel.obj.NetworkMessage
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component

class StevesLibDisconnectS2C : NetworkMessage {
    val reason: Component

    constructor(reason: Component) {
        this.reason = reason
    }

    constructor(buf: FriendlyByteBuf) {
        this.reason = buf.readComponent()
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeComponent(this.reason)
    }

    override fun handle(connection: NetworkChannelConnection) {
        connection.connection.getConnectionData(StevesLibConnectionData.KEY).customDisconnect = true
        connection.connection.connection.disconnect(this.reason)
    }
}