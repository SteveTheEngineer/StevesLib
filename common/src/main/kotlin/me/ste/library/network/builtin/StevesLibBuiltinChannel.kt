package me.ste.library.network.builtin

import dev.architectury.utils.Env
import me.ste.library.StevesLib
import me.ste.library.network.ConnectionStatus
import me.ste.library.network.PacketSinks
import me.ste.library.network.StevesLibConnection
import me.ste.library.network.channel.NetworkChannel
import me.ste.library.network.channel.NetworkChannelConnectionStatus
import me.ste.library.network.channel.obj.ObjectNetworkChannel
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket
import net.minecraft.resources.ResourceLocation

object StevesLibBuiltinChannel : ObjectNetworkChannel(
    ResourceLocation(StevesLib.MOD_ID, "builtin"),
    1,
    false
) {
    init {
        registerMessage(0, Env.CLIENT, ::StevesLibDisconnectS2C)
        registerMessage(1, Env.CLIENT, ::MenuDataS2C)
        registerMessage(2, Env.CLIENT, ::AdditionalSpawnDataS2C)
    }

    override fun onConnectionFinalStatusEvent(connection: StevesLibConnection) {
        super.onConnectionFinalStatusEvent(connection)
        
        if (connection.status == ConnectionStatus.READY) {
            connection.addConnectionData(StevesLibConnectionData.KEY, StevesLibConnectionData())
        }
    }

    fun disconnect(connection: StevesLibConnection, reason: Component) {
        if (connection.env != Env.SERVER) {
            if (!connection.hasConnectionData(StevesLibConnectionData.KEY)) {
                connection.addConnectionData(StevesLibConnectionData.KEY, StevesLibConnectionData())
            }

            connection.getConnectionData(StevesLibConnectionData.KEY).customDisconnect = true
            connection.vanillaConnection.disconnect(reason)
            return
        }

        if (
            connection.hasConnectionData(this.connectionDataKey) &&
            this.getConnection(connection).status == NetworkChannelConnectionStatus.READY
        ) {
            this.send(PacketSinks.connection(connection), StevesLibDisconnectS2C(reason))
        } else if (connection.protocol == ConnectionProtocol.LOGIN) {
            connection.vanillaConnection.send(ClientboundLoginDisconnectPacket(reason))
        } else if (connection.protocol == ConnectionProtocol.PLAY) {
            connection.vanillaConnection.send(ClientboundDisconnectPacket(reason))
        }

        connection.vanillaConnection.disconnect(reason)
    }

    override fun getDependencies(connection: StevesLibConnection): Iterable<NetworkChannel> = emptyList()
}