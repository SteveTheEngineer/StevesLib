package me.ste.library.network

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.internal.ConnectionMixinExtension
import me.ste.library.network.data.ConnectionDataKey
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.login.ClientLoginPacketListener
import net.minecraft.network.protocol.status.ClientStatusPacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.minecraft.server.network.ServerPlayerConnection
import java.util.UUID
import java.util.function.Consumer

class StevesLibConnection(
    val vanillaConnection: Connection
) {
    companion object {
        fun get(connection: Connection) = (connection as ConnectionMixinExtension).steveslib_connection

        fun get(connection: ServerLoginPacketListenerImpl) = get(connection.connection)
        fun get(connection: ServerGamePacketListenerImpl) = get(connection.connection)
        fun get(connection: ClientPacketListener) = get(connection.connection)
        fun get(connection: ClientHandshakePacketListenerImpl) = get(connection.connection)
    }

    private val handlers: Multimap<ResourceLocation, Consumer<FriendlyByteBuf>> = HashMultimap.create()

    var status = ConnectionStatus.NONE
    private val supportedChannels = mutableSetOf<ResourceLocation>()

    private val customData = mutableMapOf<UUID, Any>()
    val env get() = when (this.vanillaConnection.packetListener) {
        is ServerPacketListener -> Env.SERVER

        is ClientLoginPacketListener,
        is ClientGamePacketListener,
        is ClientStatusPacketListener -> Env.CLIENT

        else -> throw IllegalStateException()
    }
    val protocol get() = (this.vanillaConnection as ConnectionMixinExtension).steveslib_protocol
    val player get(): ServerPlayer? {
        val listener = this.vanillaConnection.packetListener

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

        // Version
        buf.writeVarInt(StevesLibNetwork.PROTOCOL_VERSION)

        // Then channels
        buf.writeMap(StevesLibNetwork.CHANNELS_I2RL, FriendlyByteBuf::writeVarInt, FriendlyByteBuf::writeResourceLocation)

        this.vanillaConnection.send(StevesLibNetwork.createRawDataPacket(this.env, this.protocol, buf))
    }

    fun handleRawData(data: FriendlyByteBuf?) {
        when (this.status) {
            ConnectionStatus.READY -> {
                if (data == null) {
                    throw IllegalArgumentException("Received a non-understood response from a ready connection.")
                }

                val channelIdInt = data.readVarInt()
                val channelIdLocation = StevesLibNetwork.CHANNELS_I2RL[channelIdInt]
                    ?: throw IllegalStateException("Received a message for an unknown channel ID: $channelIdInt")

                val channelData = data.readBytes(data.readableBytes())

                this.handleChannelMessage(channelIdLocation, FriendlyByteBuf(channelData))
            }

            ConnectionStatus.NONE -> {
                if (this.env == Env.SERVER) {
                    throw IllegalStateException("Received a login message on server before a negotiation has been started.")
                }

                // First match the version
                val version = data!!.readVarInt()

                val buf = FriendlyByteBuf(Unpooled.buffer())

                if (version != StevesLibNetwork.PROTOCOL_VERSION) {
                    this.status = ConnectionStatus.INCOMPATIBLE
                    StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                    buf.writeBoolean(false)
                    this.vanillaConnection.send(StevesLibNetwork.createRawDataPacket(this.env, this.protocol, buf))

                    return
                }

                // Next decode the channels
                val channelMap = data.readMap(FriendlyByteBuf::readVarInt, FriendlyByteBuf::readResourceLocation)

                val localChannels = mutableSetOf<ResourceLocation>()
                StevesLibNetworkEvent.REGISTER_CHANNELS.invoker().register(localChannels::add)

                for ((idInt, idLocation) in channelMap) {
                    StevesLibNetwork.CHANNELS_I2RL[idInt] = idLocation
                    StevesLibNetwork.CHANNELS_RL2I[idLocation] = idInt

                    if (idLocation in localChannels) {
                        this.supportedChannels += idLocation
                    }
                }

                // Confirm the connection status to the server
                this.status = ConnectionStatus.READY

                buf.writeBoolean(true)
                buf.writeCollection(this.supportedChannels.map { StevesLibNetwork.CHANNELS_RL2I[it]!! }, FriendlyByteBuf::writeVarInt)
                this.vanillaConnection.send(StevesLibNetwork.createRawDataPacket(this.env, this.protocol, buf))

                StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)
            }

            ConnectionStatus.NEGOTIATING -> {
                if (this.env == Env.CLIENT) {
                    throw AssertionError()
                }

                // When the message was not understood
                if (data == null) {
                    this.status = ConnectionStatus.UNSUPPORTED
                    StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                    return
                }

                // Now in case of an incompatible client
                val success = data.readBoolean()
                if (!success) {
                    this.status = ConnectionStatus.INCOMPATIBLE
                    StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)

                    return
                }

                // Assign the supported channels
                val supportedChannels = data.readCollection(::HashSet, FriendlyByteBuf::readVarInt)

                for (channel in supportedChannels) {
                    this.supportedChannels += StevesLibNetwork.CHANNELS_I2RL[channel] ?: continue
                }

                // Confirm the connection
                this.status = ConnectionStatus.READY
                StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.invoker().finalStatus(this)
            }

            else -> throw IllegalStateException("Received an invalid message.")
        }
    }

    private fun handleChannelMessage(channelId: ResourceLocation, data: FriendlyByteBuf) {
        val handlers = this.handlers[channelId]

        for (handler in handlers) {
            handler.accept(FriendlyByteBuf(data.copy()))
        }
    }

    fun registerHandler(channelId: ResourceLocation, callback: Consumer<FriendlyByteBuf>) {
        this.handlers.put(channelId, callback)
    }

    fun removeHandler(channelId: ResourceLocation, callback: Consumer<FriendlyByteBuf>) {
        this.handlers.remove(channelId, callback)
    }

    fun removeAllHandlers(channelId: ResourceLocation) {
        this.handlers.removeAll(channelId)
    }

    fun hasHandler(channelId: ResourceLocation, callback: Consumer<FriendlyByteBuf>) = this.handlers.containsEntry(channelId, callback)

    fun hasHandler(channelId: ResourceLocation) = this.handlers.containsKey(channelId)

    fun isChannelSupported(channelId: ResourceLocation) = channelId in this.supportedChannels

    fun getSupportedChannels(): Set<ResourceLocation> = this.supportedChannels


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