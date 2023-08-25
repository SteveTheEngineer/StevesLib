package me.ste.library.network

import dev.architectury.networking.NetworkManager
import dev.architectury.networking.transformers.PacketSink

class PacketSinkWithSide(
    val side: NetworkManager.Side,
    sink: PacketSink
) : PacketSink by sink