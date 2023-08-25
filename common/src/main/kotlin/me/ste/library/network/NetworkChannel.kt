package me.ste.library.network

import dev.architectury.networking.NetworkManager
import dev.architectury.networking.transformers.PacketSink
import dev.architectury.networking.transformers.SplitPacketTransformer
import io.netty.buffer.Unpooled
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.function.Function

class NetworkChannel(
    val id: ResourceLocation
) {
    val handlers = mutableListOf<NetworkMessageHandler<*>>()

    fun registerReceivers() {
        for (side in NetworkManager.Side.values()) {
            NetworkManager.registerReceiver(side, this.id, listOf(SplitPacketTransformer())) { buf, ctx ->
                val messageId = buf.readVarInt()
                val handler = this.handlers.find { it.side == side && it.id == messageId }
                    ?: throw IllegalStateException("Invalid $side message ID for channel \"${this.id}\": $messageId")

                val message = handler.decode(buf)
                (handler as NetworkMessageHandler<Any?>).handle(message, ctx)
            }
        }
    }

    fun register(handler: NetworkMessageHandler<*>) {
        this.handlers += handler
    }

    fun <T : NetworkMessage> register(id: Int, side: NetworkManager.Side, clazz: Class<T>, decode: Function<FriendlyByteBuf, T>) {
        this.register(
            object : NetworkMessageHandler<T>(id, clazz, side) {
                override fun encode(message: T, buf: FriendlyByteBuf) {
                    message.encode(buf)
                }

                override fun decode(buf: FriendlyByteBuf) = decode.apply(buf)

                override fun handle(message: T, context: NetworkManager.PacketContext) {
                    message.handle(context)
                }

            }
        )
    }

    inline fun <reified T : NetworkMessage> register(id: Int, side: NetworkManager.Side, decode: Function<FriendlyByteBuf, T>) {
        this.register(id, side, T::class.java, decode)
    }

    fun send(sink: PacketSinkWithSide, message: Any) {
        val side = sink.side
        val buf = FriendlyByteBuf(Unpooled.buffer())

        val handler = this.handlers.find { it.side == side && it.clazz == message.javaClass }
            ?: throw IllegalStateException("Invalid $side message type for channel \"${this.id}\": ${message.javaClass}")

        buf.writeVarInt(handler.id)
        (handler as NetworkMessageHandler<Any?>).encode(message, buf)

        NetworkManager.collectPackets(sink, side, this.id, buf)
    }
}