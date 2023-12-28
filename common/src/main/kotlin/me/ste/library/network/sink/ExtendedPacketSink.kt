package me.ste.library.network.sink

import dev.architectury.networking.transformers.PacketSink
import dev.architectury.utils.Env
import me.ste.library.network.StevesLibConnection
import net.minecraft.network.ConnectionProtocol

interface ExtendedPacketSink : PacketSink {
    val sendingEnv: Env
    val protocol: ConnectionProtocol

    class Wrapper(
        override val sendingEnv: Env,
        override val protocol: ConnectionProtocol,
        sink: PacketSink
    ) : ExtendedPacketSink, PacketSink by sink {
        companion object {
            fun from(connection: StevesLibConnection, sink: PacketSink) =
                Wrapper(connection.env, connection.protocol, sink)
        }
    }
}