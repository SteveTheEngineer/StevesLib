package me.ste.library.network.channel

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import dev.architectury.event.EventResult
import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.network.ConnectionStatus
import me.ste.library.network.StevesLibConnection
import me.ste.library.network.StevesLibNetwork
import me.ste.library.network.StevesLibNetworkEvent
import me.ste.library.network.data.ConnectionDataKey
import net.minecraft.network.Connection
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import java.util.function.Consumer

abstract class NetworkChannel {
    protected open val finalStatusListeners: Multimap<Connection, Consumer<NetworkChannelConnection>> = HashMultimap.create()

    abstract val channelId: ResourceLocation
    abstract val protocolVersion: Int

    protected val connectionDataKey = ConnectionDataKey<NetworkChannelConnection>()

    abstract fun checkCompatibility(localVersion: Int, remotePresent: Boolean, remoteVersion: Int): Boolean


    protected fun onFinalStatusInternal(connection: NetworkChannelConnection) {
        this.onFinalStatus(connection)

        for (listener in this.finalStatusListeners.removeAll(connection.libraryConnection.vanillaConnection)) {
            listener.accept(connection)
        }
    }


    open fun addFinalStatusCallback(connection: StevesLibConnection, callback: Consumer<NetworkChannelConnectionStatus>) {
        if (connection.hasConnectionData(this.connectionDataKey)) {
            val channelConnection = this.getConnection(connection)
            if (channelConnection.status != NetworkChannelConnectionStatus.NEGOTIATING) {
                callback.accept(channelConnection.status)
                return
            }
        }

        this.finalStatusListeners.put(connection.vanillaConnection) {
            callback.accept(it.status)
        }
    }

    abstract fun onFinalStatus(connection: NetworkChannelConnection)

    abstract fun onData(connection: NetworkChannelConnection, data: FriendlyByteBuf)

    open fun getConnection(connection: StevesLibConnection) = connection.getConnectionData(this.connectionDataKey)

    open fun register() {
        StevesLibNetworkEvent.CONNECTION_FINAL_STATUS.register(this::onConnectionFinalStatusEvent)
        StevesLibNetworkEvent.CONNECTION_END.register(this::onConnectionEndEvent)
        StevesLibNetworkEvent.LOGIN_READY_TO_ACCEPT.register(this::onLoginReadyToAcceptEvent)
        StevesLibNetworkEvent.REGISTER_CHANNELS.register(this::onRegisterChannelsEvent)
    }

    protected open fun getDependencies(connection: StevesLibConnection): Iterable<NetworkChannel> = emptyList()

    private fun onRegisterChannelsEvent(consumer: Consumer<ResourceLocation>) {
        consumer.accept(this.channelId)
    }

    protected open fun onLoginReadyToAcceptEvent(handler: ServerLoginPacketListenerImpl): EventResult {
        val connection = StevesLibConnection.get(handler.connection)

        if (!connection.hasConnectionData(this.connectionDataKey)) {
            return EventResult.interruptFalse()
        }

        val channelConnection = this.getConnection(connection)

        if (channelConnection.status != NetworkChannelConnectionStatus.NEGOTIATING) {
            return EventResult.pass()
        }

        return EventResult.interruptFalse()
    }

    protected open fun onConnectionEndEvent(connection: StevesLibConnection) {

    }

    protected open fun onConnectionFinalStatusEvent(connection: StevesLibConnection) {
        val dependencies = this.getDependencies(connection).toMutableList()

        if (dependencies.isEmpty()) {
            this.newConnection(connection)
            return
        }

        for (dependency in dependencies) {
            dependency.addFinalStatusCallback(connection) {
                dependencies.remove(dependency)

                if (dependencies.isEmpty()) {
                    this.newConnection(connection)
                }
            }
        }
    }


    protected open fun newConnection(connection: StevesLibConnection) {
        val channelConnection = NetworkChannelConnection(this, connection)

        connection.addConnectionData(this.connectionDataKey, channelConnection)

        if (connection.status != ConnectionStatus.READY || !connection.isChannelSupported(this.channelId)) {
            channelConnection.status = NetworkChannelConnectionStatus.UNSUPPORTED
            this.onFinalStatusInternal(channelConnection)

            return
        }

        connection.registerHandler(this.channelId) { this.onIncomingData(connection, it) }

        if (connection.env != Env.SERVER) {
            return
        }

        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeVarInt(this.protocolVersion)

        connection.vanillaConnection.send(StevesLibNetwork.createChannelPacket(Env.SERVER, connection.protocol, this.channelId, buf))
    }
    private fun onIncomingData(connection: StevesLibConnection, data: FriendlyByteBuf) {
        val channelConnection = this.getConnection(connection)

        if (channelConnection.status == NetworkChannelConnectionStatus.READY) {
            this.onData(channelConnection, data)
            return
        }

        if (channelConnection.status != NetworkChannelConnectionStatus.NEGOTIATING) {
            return
        }

        when (connection.env) {
            Env.CLIENT -> {
                val version = data.readVarInt()

                val buf = FriendlyByteBuf(Unpooled.buffer())

                if (!this.checkCompatibility(this.protocolVersion, true, version)) {
                    buf.writeBoolean(false)
                    connection.vanillaConnection.send(StevesLibNetwork.createChannelPacket(Env.CLIENT, connection.protocol, this.channelId, buf))

                    channelConnection.status = NetworkChannelConnectionStatus.INCOMPATIBLE
                    this.onFinalStatusInternal(channelConnection)

                    return
                }

                buf.writeBoolean(true)
                connection.vanillaConnection.send(StevesLibNetwork.createChannelPacket(Env.CLIENT, connection.protocol, this.channelId, buf))

                channelConnection.status = NetworkChannelConnectionStatus.READY
                this.onFinalStatusInternal(channelConnection)
            }

            Env.SERVER -> {
                val success = data.readBoolean()

                if (!success) {
                    channelConnection.status = NetworkChannelConnectionStatus.INCOMPATIBLE
                    this.onFinalStatusInternal(channelConnection)

                    return
                }

                channelConnection.status = NetworkChannelConnectionStatus.READY
                this.onFinalStatusInternal(channelConnection)
            }
        }
    }

    protected fun createPacket(sendingEnv: Env, protocol: ConnectionProtocol, data: FriendlyByteBuf) =
        StevesLibNetwork.createChannelPacket(sendingEnv, protocol, this.channelId, data)
}