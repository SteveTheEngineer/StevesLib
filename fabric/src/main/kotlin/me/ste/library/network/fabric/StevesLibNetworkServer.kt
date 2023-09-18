package me.ste.library.network.fabric

import io.netty.buffer.Unpooled
import me.ste.library.internal.network2.ConnectionStatus
import me.ste.library.internal.network2.StevesLibConnection
import me.ste.library.internal.network2.StevesLibNetworkInternals
import me.ste.library.network2.StevesLibNetworkEvent
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import org.apache.logging.log4j.LogManager

object StevesLibNetworkServer {
    private val LOGGER = LogManager.getLogger(this)

    fun register() {
        ServerLoginConnectionEvents.QUERY_START.register(this::onQueryStart)

        ServerLoginNetworking.registerGlobalReceiver(StevesLibNetworkInternals.CHANNEL_ID, this::handleResponse)

        ServerPlayNetworking.registerGlobalReceiver(StevesLibNetworkInternals.CHANNEL_ID, this::onPlayPacket)
    }

    private fun onPlayPacket(
        server: MinecraftServer,
        player: ServerPlayer,
        handler: ServerGamePacketListenerImpl,
        buf: FriendlyByteBuf,
        sender: PacketSender
    ) {
        val connection = StevesLibConnection.get(handler.connection)
        connection.handleRawData(buf)
    }


    private fun onQueryStart(handler: ServerLoginPacketListenerImpl, server: MinecraftServer, sender: PacketSender, synchronizer: ServerLoginNetworking.LoginSynchronizer) {
        val connection = StevesLibConnection.get(handler.connection)
        connection.status = ConnectionStatus.NEGOTIATING_COMPATIBILITY

        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeVarInt(StevesLibNetworkInternals.PROTOCOL_VERSION)

        sender.sendPacket(StevesLibNetworkInternals.CHANNEL_ID, buf)
    }

    private fun handleResponse(
        server: MinecraftServer,
        handler: ServerLoginPacketListenerImpl,
        understood: Boolean,
        buf: FriendlyByteBuf,
        synchronizer: ServerLoginNetworking.LoginSynchronizer,
        responseSender: PacketSender
    ) {
        val connection = StevesLibConnection.get(handler.connection)

        if (connection.status == ConnectionStatus.NEGOTIATING_COMPATIBILITY) {
            this.handleVersionResponse(server, handler, understood, buf, synchronizer, responseSender)
        } else if (connection.status == ConnectionStatus.NEGOTIATING_RESERVATION) {
            if (understood) {
                connection.status = ConnectionStatus.READY
                StevesLibNetworkEvent.LOGIN_CONNECTION_READY.invoker().ready(connection)
            } else {
                connection.status = ConnectionStatus.ERROR
            }
        }
    }

    private fun handleVersionResponse(
        server: MinecraftServer,
        handler: ServerLoginPacketListenerImpl,
        understood: Boolean,
        buf: FriendlyByteBuf,
        synchronizer: ServerLoginNetworking.LoginSynchronizer,
        responseSender: PacketSender
    ) {
        val connection = StevesLibConnection.get(handler.connection)

        if (!understood) {
            connection.status = ConnectionStatus.UNSUPPORTED
            return
        }

        val compatible = buf.readBoolean()
        if (!compatible) {
            connection.status = ConnectionStatus.INCOMPATIBLE
            return
        }

        val packetBuf = FriendlyByteBuf(Unpooled.buffer())
        packetBuf.writeBoolean(true)
        val packet = responseSender.createPacket(StevesLibNetworkInternals.CHANNEL_ID, FriendlyByteBuf(packetBuf))

        if (packet !is ClientboundCustomQueryPacket) {
            LOGGER.warn("Unable to complete the StevesLib network handshake for login handler $handler (username ${handler.userName}) due to an incompatible server Fabric network implementation. Will now mark the connection as errored.")
            connection.status = ConnectionStatus.ERROR

            val packetBuf2 = FriendlyByteBuf(Unpooled.buffer())
            packetBuf2.writeBoolean(false)
            responseSender.sendPacket(StevesLibNetworkInternals.CHANNEL_ID, packetBuf2)

            return
        }

        connection.status = ConnectionStatus.NEGOTIATING_RESERVATION
        connection.loginTransactionId = packet.transactionId

        responseSender.sendPacket(packet)
    }
}