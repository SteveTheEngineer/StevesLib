package me.ste.library.network.channel.obj

import me.ste.library.network.channel.NetworkChannelConnection
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

interface ServerNetworkMessage : NetworkMessage {
    override fun handle(connection: NetworkChannelConnection) {
        val player = connection.libraryConnection.player ?: return
        val server = player.server

        server.execute {
            this.handle(connection, server, player)
        }
    }

    fun handle(connection: NetworkChannelConnection, server: MinecraftServer, player: ServerPlayer)
}