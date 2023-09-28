package me.ste.library.network

import dev.architectury.utils.Env
import me.ste.library.StevesLib
import me.ste.library.network.builtin.StevesLibBuiltinChannel
import me.ste.library.network.builtin.StevesLibDisconnectS2C
import net.minecraft.network.ConnectionProtocol
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket
import net.minecraft.resources.ResourceLocation

object StevesLibNetwork {
    val CHANNEL_ID = ResourceLocation(StevesLib.MOD_ID, "network")
    val TRANSACTION_ID = -1398029378

    val PROTOCOL_VERSION = 0
}