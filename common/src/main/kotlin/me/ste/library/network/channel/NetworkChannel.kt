package me.ste.library.network.channel

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.network.ConnectionStatus
import me.ste.library.network.StevesLibConnection
import me.ste.library.network.StevesLibNetworkEvent
import me.ste.library.network.data.ConnectionDataKey
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
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

        for (listener in this.finalStatusListeners.removeAll(connection.connection.connection)) {
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

        this.finalStatusListeners.put(connection.connection) {
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
    }


    open protected fun getDependencies(connection: StevesLibConnection): Iterable<NetworkChannel> = emptyList()

    protected open fun onLoginReadyToAcceptEvent(handler: ServerLoginPacketListenerImpl, interrupt: Runnable) {
        val connection = StevesLibConnection.get(handler.connection)

        if (!connection.hasConnectionData(this.connectionDataKey)) {
            interrupt.run()
            return
        }

        val channelConnection = this.getConnection(connection)

        if (channelConnection.status != NetworkChannelConnectionStatus.NEGOTIATING) {
            return
        }

        interrupt.run()
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

        if (connection.status != ConnectionStatus.READY) {
            channelConnection.status = NetworkChannelConnectionStatus.UNSUPPORTED
            this.onFinalStatusInternal(channelConnection)

            return
        }

        connection.registerHandler(this.channelId) { this.onIncomingData(connection, it) }

        if (connection.env != Env.SERVER) {
            return
        }

        connection.registerUnrecognizedHandler(this.channelId) { this.onMessageUnrecognized(connection) }

        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeVarInt(this.protocolVersion)

        connection.sendChannelMessage(this.channelId, buf)
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
                    connection.sendChannelMessage(this.channelId, buf)

                    channelConnection.status = NetworkChannelConnectionStatus.INCOMPATIBLE
                    this.onFinalStatusInternal(channelConnection)

                    return
                }

                buf.writeBoolean(true)
                connection.sendChannelMessage(this.channelId, buf)

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

    private fun onMessageUnrecognized(connection: StevesLibConnection) {
        if (connection.env != Env.SERVER) {
            return
        }

        val channelConnection = this.getConnection(connection)

        channelConnection.status = NetworkChannelConnectionStatus.UNSUPPORTED
        this.onFinalStatusInternal(channelConnection)
    }
}