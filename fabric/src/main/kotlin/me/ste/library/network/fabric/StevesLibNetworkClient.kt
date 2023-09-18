package me.ste.library.network.fabric

import io.netty.buffer.Unpooled
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import me.ste.library.internal.network2.ConnectionStatus
import me.ste.library.internal.network2.StevesLibConnection
import me.ste.library.internal.network2.StevesLibNetworkInternals
import me.ste.library.network2.StevesLibNetworkEvent
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.FriendlyByteBuf
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object StevesLibNetworkClient {
    fun register() {
        ClientLoginConnectionEvents.QUERY_START.register(this::onQueryStart)
        ClientPlayConnectionEvents.INIT.register(this::onPlayInit)

        ClientPlayNetworking.registerGlobalReceiver(StevesLibNetworkInternals.CHANNEL_ID, this::onPlayPacket)
    }

    private fun onPlayPacket(
        client: Minecraft,
        handler: ClientPacketListener,
        buf: FriendlyByteBuf,
        sender: PacketSender
    ) {
        val connection = StevesLibConnection.get(handler.connection)
        connection.handleRawData(buf)
    }

    private fun onPlayInit(handler: ClientPacketListener, client: Minecraft) {
        val connection = StevesLibConnection.get(handler.connection)

        if (connection.status != ConnectionStatus.NONE) {
            return
        }

        connection.status = ConnectionStatus.UNSUPPORTED
    }

    private fun onQueryStart(handler: ClientHandshakePacketListenerImpl, client: Minecraft) {
        ClientLoginNetworking.registerReceiver(StevesLibNetworkInternals.CHANNEL_ID, this::onVersionPacket)
    }

    private fun onVersionPacket(
        client: Minecraft,
        handler: ClientHandshakePacketListenerImpl,
        buf: FriendlyByteBuf,
        listenerAdder: Consumer<GenericFutureListener<out Future<in Void>>>
    ): CompletableFuture<FriendlyByteBuf?> {
        ClientLoginNetworking.unregisterReceiver(StevesLibNetworkInternals.CHANNEL_ID)

        val connection = StevesLibConnection.get(handler.connection)

        val version = buf.readVarInt()
        if (version != StevesLibNetworkInternals.PROTOCOL_VERSION) {
            connection.status = ConnectionStatus.INCOMPATIBLE

            val packetBuf = FriendlyByteBuf(Unpooled.buffer())
            packetBuf.writeBoolean(false)
            return CompletableFuture.completedFuture(packetBuf)
        }

        ClientLoginNetworking.registerReceiver(StevesLibNetworkInternals.CHANNEL_ID, this::onReservationPacket)
        connection.status = ConnectionStatus.NEGOTIATING_RESERVATION

        val packetBuf = FriendlyByteBuf(Unpooled.buffer())
        packetBuf.writeBoolean(true)
        return CompletableFuture.completedFuture(packetBuf)
    }

    private fun onReservationPacket(
        client: Minecraft,
        handler: ClientHandshakePacketListenerImpl,
        buf: FriendlyByteBuf,
        listenerAdder: Consumer<GenericFutureListener<out Future<in Void>>>
    ): CompletableFuture<FriendlyByteBuf?> {
        ClientLoginNetworking.unregisterReceiver(StevesLibNetworkInternals.CHANNEL_ID)

        val connection = StevesLibConnection.get(handler.connection)

        val success = buf.readBoolean()
        if (success) {
            connection.status = ConnectionStatus.READY

            listenerAdder.accept {
                StevesLibNetworkEvent.LOGIN_CONNECTION_READY.invoker().ready(connection)
            }
        } else {
            connection.status = ConnectionStatus.ERROR
        }

        return CompletableFuture.completedFuture(FriendlyByteBuf(Unpooled.EMPTY_BUFFER))
    }
}