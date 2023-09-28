package me.ste.library.network

import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.internal.ConnectionMixinExtension
import me.ste.library.network.data.ConnectionDataKey
import net.minecraft.network.Connection
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket
import net.minecraft.network.protocol.login.ClientLoginPacketListener
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket
import net.minecraft.network.protocol.status.ClientStatusPacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

class StevesLibConnection(
    val connection: Connection
) {
    companion object {
        fun get(connection: Connection) = (connection as ConnectionMixinExtension).steveslib_connection
    }

    private val handlers = mutableMapOf<ResourceLocation, Consumer<FriendlyByteBuf>>()
    private val unrecognizedHandlers = mutableMapOf<ResourceLocation, Runnable>()

    var status = ConnectionStatus.NONE

    private val lastChannelId = AtomicInteger()

    private val localChannels = mutableMapOf<ResourceLocation, Int>()
    private val remoteChannels = mutableMapOf<Int, ResourceLocation>()

    private val customData = mutableMapOf<UUID, Any>()
    val env get() = when (this.connection.packetListener) {
        is ServerPacketListener -> Env.SERVER

        is ClientLoginPacketListener,
        is ClientGamePacketListener,
        is ClientStatusPacketListener -> Env.CLIENT

        else -> throw IllegalStateException()
    }
    val protocol get() = (this.connection as ConnectionMixinExtension).steveslib_protocol
    val player get(): ServerPlayer? {
        val listener = this.connection.packetListener

        if (listener !is ServerPlayerConnection) {
            return null
        }

        return listener.player
    }

    fun startNegotiation() {
        if (this.status != ConnectionStatus.NONE) {
            throw IllegalStateException("The negotiation has already been started.")
        }

        this.status = ConnectionStatus.NEGOTIATING

        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeVarInt(StevesLibNetwork.PROTOCOL_VERSION)

        this.sendRawData(buf)
    }

    private fun sendRawData(data: FriendlyByteBuf) {
        val packet: Packet<*> = when (this.protocol) {
            ConnectionProtocol.LOGIN -> when (this.env) {
                Env.CLIENT -> ServerboundCustomQueryPacket(StevesLibNetwork.TRANSACTION_ID, data)
                Env.SERVER -> ClientboundCustomQueryPacket(StevesLibNetwork.TRANSACTION_ID, StevesLibNetwork.CHANNEL_ID, data)
            }

            ConnectionProtocol.PLAY -> when (this.env) {
                Env.CLIENT -> ServerboundCustomPayloadPacket(StevesLibNetwork.CHANNEL_ID, data)
                Env.SERVER -> ClientboundCustomPayloadPacket(StevesLibNetwork.CHANNEL_ID, data)
            }

            else -> throw IllegalStateException("Invalid connection.")
        }

        this.connection.send(packet)
    }

    fun handleRawData(data: FriendlyByteBuf?) {
        when (this.status) {
            ConnectionStatus.READY -> {
                if (data == null) {
                    throw IllegalArgumentException("Received a non-understood response from a ready connection.")
                }

                val messageType = data.readVarInt()
                val messageData = data.readBytes(data.readableBytes())

                this.handleRawMessage(messageType, FriendlyByteBuf(messageData))
            }

            ConnectionStatus.NONE -> {
                if (this.env == Env.SERVER) {
                    throw IllegalStateException("Received a login message on server before a negotiation has been started.")
                }

                val version = data!!.readVarInt()
                val buf = FriendlyByteBuf(Unpooled.buffer())

                if (version != StevesLibNetwork.PROTOCOL_VERSION) {
                    this.status = ConnectionStatus.INCOMPATIBLE
                    StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                    buf.writeBoolean(false)
                    this.sendRawData(buf)

                    return
                }

                this.status = ConnectionStatus.READY
                StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                buf.writeBoolean(true)
                this.sendRawData(buf)
            }

            ConnectionStatus.NEGOTIATING -> {
                if (this.env == Env.CLIENT) {
                    throw AssertionError()
                }

                if (data == null) {
                    this.status = ConnectionStatus.UNSUPPORTED
                    StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                    return
                }

                val success = data.readBoolean()
                if (!success) {
                    this.status = ConnectionStatus.INCOMPATIBLE
                    StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                    return
                }

                this.status = ConnectionStatus.READY
                StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)
            }

            else -> throw IllegalStateException("Received an invalid message.")
        }
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
        if (this.status != ConnectionStatus.READY) {
            throw IllegalStateException("Unable to send data due to the connection not being ready.")
        }

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
        if (channelId in this.unrecognizedHandlers) {
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


    fun <T : Any> addConnectionData(key: ConnectionDataKey<T>, data: T) {
        if (key.id in this.customData) {
            throw IllegalArgumentException("The connection data for key ${key.id} already exists.")
        }

        this.customData[key.id] = data
    }


    fun hasConnectionData(key: ConnectionDataKey<*>) = key.id in this.customData


    fun <T : Any> getConnectionData(key: ConnectionDataKey<T>): T {
        val data = this.customData[key.id]
            ?: throw IllegalArgumentException("The connection data for key ${key.id} is missing.")

        return data as T
    }
}