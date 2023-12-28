package me.ste.library.network.channel

import me.ste.library.network.StevesLibConnection

class NetworkChannelConnection(
    val channel: NetworkChannel,
    val libraryConnection: StevesLibConnection
) {
    var status = NetworkChannelConnectionStatus.NEGOTIATING
}