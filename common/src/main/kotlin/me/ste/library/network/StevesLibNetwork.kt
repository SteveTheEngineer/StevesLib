package me.ste.library.network

import dev.architectury.utils.Env
import io.netty.buffer.Unpooled
import me.ste.library.StevesLib
import me.ste.library.network.builtin.StevesLibBuiltinChannel
import me.ste.library.network.builtin.StevesLibDisconnectS2C
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket
import net.minecraft.resources.ResourceLocation

object StevesLibNetwork {
    val CHANNEL_ID = ResourceLocation(StevesLib.MOD_ID, "network")
    val TRANSACTION_ID = -1398029378

    val PROTOCOL_VERSION = 1

    val CHANNELS_I2RL = mutableMapOf<Int, ResourceLocation>()
    val CHANNELS_RL2I = mutableMapOf<ResourceLocation, Int>()

    fun createRawDataPacket(sendingEnv: Env, protocol: ConnectionProtocol, data: FriendlyByteBuf): Packet<*> {
        return when (protocol) {
            ConnectionProtocol.LOGIN -> when (sendingEnv) {
                Env.CLIENT -> ServerboundCustomQueryPacket(TRANSACTION_ID, data)
                Env.SERVER -> ClientboundCustomQueryPacket(TRANSACTION_ID, CHANNEL_ID, data)
            }

            ConnectionProtocol.PLAY -> when (sendingEnv) {
                Env.CLIENT -> ServerboundCustomPayloadPacket(CHANNEL_ID, data)
                Env.SERVER -> ClientboundCustomPayloadPacket(CHANNEL_ID, data)
            }

            else -> throw IllegalStateException("Invalid connection.")
        }
    }

    fun createChannelPacket(sendingEnv: Env, protocol: ConnectionProtocol, channelId: ResourceLocation, data: FriendlyByteBuf): Packet<*> {
        val channelIdInt = CHANNELS_RL2I[channelId]
            ?: throw IllegalStateException("Channel ID $channelId is not registered.")

        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeVarInt(channelIdInt)
        buf.writeBytes(data, data.readableBytes())

        return createRawDataPacket(sendingEnv, protocol, buf)
    }
}