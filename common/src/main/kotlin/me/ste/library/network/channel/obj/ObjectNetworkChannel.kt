package me.ste.library.network.channel.obj

import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.network.StevesLibConnection
import me.ste.library.network.builtin.StevesLibBuiltinChannel
import me.ste.library.network.channel.NetworkChannel
import me.ste.library.network.channel.NetworkChannelConnection
import me.ste.library.network.channel.NetworkChannelConnectionStatus
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.util.function.BiConsumer
import java.util.function.Function

open class ObjectNetworkChannel(
    override val channelId: ResourceLocation,
    override val protocolVersion: Int
) : NetworkChannel() {
    protected val idMapC2S = mutableMapOf<Int, ObjectNetworkChannelRegistration<*>>()
    protected val idMapS2C = mutableMapOf<Int, ObjectNetworkChannelRegistration<*>>()

    protected val typeMapC2S = mutableMapOf<Class<*>, ObjectNetworkChannelRegistration<*>>()
    protected val typeMapS2C = mutableMapOf<Class<*>, ObjectNetworkChannelRegistration<*>>()

    override fun checkCompatibility(localVersion: Int, remotePresent: Boolean, remoteVersion: Int): Boolean {
        if (!remotePresent) {
            return false
        }

        return localVersion == remoteVersion
    }


    override fun getDependencies(connection: StevesLibConnection): Iterable<NetworkChannel> = listOf(StevesLibBuiltinChannel)

    override fun onFinalStatus(connection: NetworkChannelConnection) {
        if (connection.status == NetworkChannelConnectionStatus.READY) {
            return
        }

        StevesLibBuiltinChannel.disconnect(connection.connection, Component.literal("Failed to establish connection on channel \"${this.channelId}\"."))
    }

    fun <T> registerMessage(id: Int, type: Class<T>, receivingSide: Env, decode: Function<FriendlyByteBuf, T>, encode: BiConsumer<T, FriendlyByteBuf>, handle: BiConsumer<T, NetworkChannelConnection>) {
        val registration = ObjectNetworkChannelRegistration(id, type, decode, encode, handle)

        if (receivingSide == Env.SERVER) {
            if (id in this.idMapC2S) {
                throw IllegalArgumentException("The message ID $id for side SERVER has already been registered.")
            }

            if (type in this.typeMapC2S) {
                throw IllegalArgumentException("The message type $type for side SERVER has already been registered.")
            }

            this.idMapC2S[id] = registration
            this.typeMapC2S[type] = registration
        }

        if (receivingSide == Env.CLIENT) {
            if (id in this.idMapS2C) {
                throw IllegalArgumentException("The message ID $id for side CLIENT has already been registered.")
            }

            if (type in this.typeMapS2C) {
                throw IllegalArgumentException("The message type $type for side CLIENT has already been registered.")
            }

            this.idMapS2C[id] = registration
            this.typeMapS2C[type] = registration
        }
    }

    fun <T : NetworkMessage> registerMessage(id: Int, type: Class<T>, receivingSide: Env, decode: Function<FriendlyByteBuf, T>) {
        this.registerMessage(id, type, receivingSide, decode, NetworkMessage::encode, NetworkMessage::handle)
    }

    inline fun <reified T : NetworkMessage> registerMessage(id: Int, receivingSide: Env, decode: Function<FriendlyByteBuf, T>) {
        this.registerMessage(id, T::class.java, receivingSide, decode)
    }

    open fun sendMessage(connection: Connection, message: Any) {
        val stevesLibConnection = StevesLibConnection.get(connection)
        this.sendMessage(stevesLibConnection, message)
    }

    open fun sendMessage(connection: StevesLibConnection, message: Any) {
        val registration = when (connection.env) {
            Env.SERVER -> this.typeMapS2C[message::class.java]
            Env.CLIENT -> this.typeMapC2S[message::class.java]
        } as? ObjectNetworkChannelRegistration<Any>
            ?: throw IllegalArgumentException("The message type ${message::class.java} is not registered.")

        val channelConnection = this.getConnection(connection)
            ?: throw IllegalArgumentException("The connection has not been established yet.")

        val data = FriendlyByteBuf(Unpooled.buffer())
        registration.encode.accept(message, data)

        val messageData = FriendlyByteBuf(Unpooled.buffer())
        messageData.writeVarInt(registration.messageId)
        messageData.writeBytes(data)

        channelConnection.send(messageData)
    }

    override fun onData(connection: NetworkChannelConnection, data: FriendlyByteBuf) {
        val messageId = data.readVarInt()

        val registration = when (connection.connection.env) {
            Env.SERVER -> this.idMapC2S[messageId]
            Env.CLIENT -> this.idMapS2C[messageId]
        } as? ObjectNetworkChannelRegistration<Any>
            ?: throw IllegalArgumentException("The message ID $messageId is not registered.")

        val payload = data.readBytes(data.readableBytes())
        val message = registration.decode.apply(FriendlyByteBuf(payload))

        registration.handle.accept(message, connection)
    }
}