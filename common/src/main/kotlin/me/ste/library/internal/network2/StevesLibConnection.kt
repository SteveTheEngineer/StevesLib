package me.ste.library.internal.network2

import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.Connection
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.game.ServerGamePacketListener
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket
import net.minecraft.network.protocol.login.ClientLoginPacketListener
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket
import net.minecraft.network.protocol.status.ClientStatusPacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.server.network.ServerPlayerConnection
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Function

class StevesLibConnection(
    val connection: Connection
) {
    companion object {
        fun get(connection: Connection) = (connection as ConnectionMixinExtension).steveslib_connection
        fun get(player: ServerPlayer) = get(player.connection.connection)
        fun get(player: LocalPlayer) = get(player.connection.connection)
    }

    private val handlers = mutableMapOf<ResourceLocation, Consumer<FriendlyByteBuf>>()
    private val unrecognizedHandlers = mutableMapOf<ResourceLocation, Runnable>()

    var status = ConnectionStatus.NONE
    var loginTransactionId = 0

    private val lastChannelId = AtomicInteger()

    private val localChannels = mutableMapOf<ResourceLocation, Int>()
    private val remoteChannels = mutableMapOf<Int, ResourceLocation>()

    val env get() = when (this.connection.packetListener) {
        is ServerPacketListener -> Env.SERVER

        is ClientLoginPacketListener,
        is ClientGamePacketListener,
        is ClientStatusPacketListener -> Env.CLIENT

        else -> throw IllegalStateException()
    }

    val player get(): ServerPlayer? {
        val listener = this.connection.packetListener

        if (listener !is ServerPlayerConnection) {
            return null
        }

        return listener.player
    }

    private fun sendRawData(data: FriendlyByteBuf) {
        if (this.status != ConnectionStatus.READY) {
            throw IllegalStateException("Unable to send data due to the connection not being ready.")
        }

        val packet: Packet<*> = when ((this.connection as ConnectionMixinExtension).steveslib_protocol) {
            ConnectionProtocol.LOGIN -> when (this.env) {
                Env.CLIENT -> ServerboundCustomQueryPacket(this.loginTransactionId, data)
                Env.SERVER -> ClientboundCustomQueryPacket(this.loginTransactionId, StevesLibNetworkInternals.CHANNEL_ID, data)
            }

            ConnectionProtocol.PLAY -> when (this.env) {
                Env.CLIENT -> ServerboundCustomPayloadPacket(StevesLibNetworkInternals.CHANNEL_ID, data)
                Env.SERVER -> ClientboundCustomPayloadPacket(StevesLibNetworkInternals.CHANNEL_ID, data)
            }

            else -> throw IllegalStateException("Invalid connection.")
        }

        this.connection.send(packet)
    }

    fun handleRawData(data: FriendlyByteBuf) {
        if (this.status != ConnectionStatus.READY) {
            throw IllegalStateException("Received data on a connection that is not ready.")
        }

        val messageType = data.readVarInt()
        val messageData = data.readBytes(data.readableBytes())

        this.handleRawMessage(messageType, FriendlyByteBuf(messageData))
    }

    private fun handleRawMessage(messageType: Int, data: FriendlyByteBuf) {
        when (messageType) {
            0 -> {
                val channelId = data.readResourceLocation()
                val mappedChannelId = data.readVarInt()

                this.remoteChannels[mappedChannelId] = channelId

                val channelData = data.readBytes(data.readableBytes())
                this.handleChannelMessage(channelId, FriendlyByteBuf(channelData))
            }

            1 -> {
                val mappedChannelId = data.readVarInt()

                val channelId = this.remoteChannels[mappedChannelId]
                    ?: throw IllegalStateException("Received a message for an unknown mapped channel: $mappedChannelId")

                val channelData = data.readBytes(data.readableBytes())
                this.handleChannelMessage(channelId, FriendlyByteBuf(channelData))
            }

            2 -> {
                val channelId = data.readResourceLocation()
                this.unrecognizedHandlers[channelId]?.run()
            }
        }
    }

    private fun handleChannelMessage(channelId: ResourceLocation, data: FriendlyByteBuf) {
        val handler = this.handlers[channelId]

        if (handler == null) {
            val buf = FriendlyByteBuf(Unpooled.buffer())

            buf.writeVarInt(2)
            buf.writeResourceLocation(channelId)

            this.sendRawData(buf)
            return
        }

        handler.accept(data)
    }

    fun sendChannelMessage(channelId: ResourceLocation, data: FriendlyByteBuf) {
        val mappedChannelId = this.localChannels[channelId]

        if (mappedChannelId == null) {
            val newChannelId = this.lastChannelId.getAndIncrement()
            this.localChannels[channelId] = newChannelId

            val buf = FriendlyByteBuf(Unpooled.buffer())

            buf.writeVarInt(0)

            buf.writeResourceLocation(channelId)
            buf.writeVarInt(newChannelId)
            buf.writeBytes(data)

            this.sendRawData(buf)

            return
        }

        val buf = FriendlyByteBuf(Unpooled.buffer())

        buf.writeVarInt(1)

        buf.writeVarInt(mappedChannelId)
        buf.writeBytes(data)

        this.sendRawData(buf)

        return
    }

    fun registerHandler(channelId: ResourceLocation, callback: Consumer<FriendlyByteBuf>) {
        if (channelId in this.handlers) {
            throw IllegalArgumentException("A handler for channel ID $channelId has already been registered.")
        }

        this.handlers[channelId] = callback
    }

    fun registerUnrecognizedHandler(channelId: ResourceLocation, callback: Runnable) {
        if (channelId in this.handlers) {
            throw IllegalArgumentException("An unrecognized channel handler for channel ID $channelId has already been registered.")
        }

        this.unrecognizedHandlers[channelId] = callback
    }

    fun removeHandler(channelId: ResourceLocation) {
        this.handlers -= channelId
    }

    fun removeUnrecognizedHandler(channelId: ResourceLocation) {
        this.handlers -= channelId
    }

    fun hasHandler(channelId: ResourceLocation) = channelId in this.handlers

    fun hasUnrecognizedHandler(channelId: ResourceLocation) = channelId in this.unrecognizedHandlers
}