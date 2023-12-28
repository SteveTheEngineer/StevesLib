package me.ste.library.client.network

import me.ste.library.network.channel.NetworkChannelConnection
import me.ste.library.network.channel.obj.NetworkMessage
import net.minecraft.client.Minecraft

interface ClientNetworkMessage : NetworkMessage {
    override fun handle(connection: NetworkChannelConnection) {
        val minecraft = Minecraft.getInstance()
        minecraft.execute {
            this.handle(connection, minecraft)
        }
    }

    fun handle(connection: NetworkChannelConnection, minecraft: Minecraft)
}